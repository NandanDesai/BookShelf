import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../_misc/auth.service';
import {NotifierService} from 'angular-notifier';
import {IsLoadingService} from '@service-work/is-loading';
import {Router} from '@angular/router';
import {TokenStorageService} from '../_misc/tokenstore.service';
import {TokenPayload} from '../_models/token-payload';
import decode from 'jwt-decode';
import {ModeChangeListener} from '../_misc/mode-change.listener';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit{

  submitted = false;
  secureMode = false;
  captchaResponseToken = null;
  tooltipText = 'The following are the constraints of for the password:\n' +
    '* It contains at least 8 characters and at most 20 characters.\n' +
    '* It contains at least one digit.\n' +
    '* It contains at least one upper case alphabet.\n' +
    '* It contains at least one lower case alphabet.\n' +
    '* It contains at least one special character which includes !@#$%&*()-+=^.\n' +
    '* It doesn’t contain any white space.';

  form: FormGroup = this.fb.group({
    fullName: [null, Validators.required],
    email: [null, [Validators.email, Validators.required]],
    password: [null, Validators.required],
    confirmPassword: [null, Validators.required]
  }, {validators: [this.passwordValidator, this.passwordsMatchValidator]});

  constructor(private fb: FormBuilder,
              private authService: AuthService,
              private readonly notifier: NotifierService,
              private isLoadingService: IsLoadingService,
              private router: Router,
              private tokenService: TokenStorageService,
              private modeChangeListener: ModeChangeListener) {
  }

  ngOnInit(): void {
    this.modeChangeListener.profilePicChanged$.subscribe(
      secure => {
        if (secure){
          this.secureMode = true;
        }else{
          this.secureMode = false;
        }
      }
    );
  }

  private passwordsMatchValidator(formGroup: FormGroup): void {
    if (formGroup.controls.confirmPassword.value) {
      if (formGroup.controls.password.value !== formGroup.controls.confirmPassword.value) {
        formGroup.controls.confirmPassword.setErrors({mismatch: true});
      } else {
        formGroup.controls.confirmPassword.setErrors(null);
      }
    }
  }

  private passwordValidator(formGroup: FormGroup): void {
    if (formGroup.controls.password.value) {
      const regex: RegExp = new RegExp('^(?=.*[0-9])'
        + '(?=.*[a-z])(?=.*[A-Z])'
        + '(?=.*[!@#$%^&+=])'
        + '(?=\\S+$).{8,20}$');
      if (!regex.test(formGroup.controls.password.value)) {
        formGroup.controls.password.setErrors({invalidPass: true});
      } else {
        formGroup.controls.password.setErrors(null);
      }
    }
  }

  captchaResolved(event): void {
    this.captchaResponseToken = event;
  }

  submit(): void {
    this.submitted = true;
    if (this.secureMode && (this.captchaResponseToken == null)) {
      return;
    }
    if (this.form.valid) {
      this.isLoadingService.add();
      const userInfo = {
        email: this.form.controls.email.value,
        password: this.form.controls.password.value,
        fullName: this.form.controls.fullName.value,
        challengeToken: this.captchaResponseToken
      };
      this.authService.signup(userInfo).subscribe(
        data => {
          this.notifier.notify('success', 'Successfully Registered');
          console.log(data);
          this.tokenService.saveToken(data.payload);
          const tokenPayload: TokenPayload = decode(data.payload); // decode the jwt payload
          this.tokenService.savePayload(tokenPayload);
          console.log(tokenPayload);
          this.notifier.notify('success', 'Welcome, ' + userInfo.fullName + '!');
          this.isLoadingService.remove();

          this.router.navigate(['/dash', {}]);
        },
        err => {
          this.notifier.notify('error', 'Signup Failed');
          console.error(err);
          this.isLoadingService.remove();
        }
      );
    }
  }
}
