import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {AuthService} from '../_misc/auth.service';
import {IsLoadingService} from '@service-work/is-loading';
import {Router} from '@angular/router';
import {NotifierService} from 'angular-notifier';
import {StorageService} from '../_misc/storage.service';
import {ModeChangeListener} from '../_misc/mode-change.listener';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  submitted = false;
  secureMode = false;
  captchaResponseToken = null;

  form = this.fb.group({
    email: [null, [Validators.required]],
    password: [null, Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private readonly notifier: NotifierService,
    private isLoadingService: IsLoadingService,
    private router: Router,
    private storageService: StorageService,
    private modeChangeListener: ModeChangeListener
  ) {
    this.storageService.clearStore();
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
      const credentials = {
        email: this.form.controls.email.value,
        password: this.form.controls.password.value,
        challengeToken: this.captchaResponseToken
      };
      this.authService.login(credentials).subscribe(
        data => {
          this.notifier.notify('success', 'Login Successful');
          console.log(data);
          console.log(data.payload);
          this.storageService.saveUser(data.payload);
          this.isLoadingService.remove();

          this.router.navigate(['/dash', {}]);
        },
        err => {
          this.notifier.notify('error', 'Login Failed');
          console.error(err);
          this.isLoadingService.remove();
        }
      );
    }
  }
}

