import {Component, OnInit} from '@angular/core';
import {NotifierService} from 'angular-notifier';
import {IsLoadingService} from '@service-work/is-loading';
import {UserService} from '../_misc/user.service';
import {GlobalVars} from '../global.vars';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProfilePicChangeListener} from '../_misc/profilepic-change-listener.service';
import {StorageService} from '../_misc/storage.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserPhotoUploadComponent implements OnInit {

  userId = 0;
  userPhotoUrl = '';
  fileToUpload: File = null;
  tooltipText =
    'The following are the constraints of for the password:\n' +
    '* It contains at least 8 characters and at most 20 characters.\n' +
    '* It contains at least one digit.\n' +
    '* It contains at least one upper case alphabet.\n' +
    '* It contains at least one lower case alphabet.\n' +
    '* It contains at least one special character which includes !@#$%&*()-+=^.\n' +
    '* It doesn’t contain any white space.';

  submitted = false;
  form = this.fb.group(
    {
      curPassword: [null, Validators.required],
      password: [null, Validators.required],
      confirmPassword: [null, Validators.required]
    },
    {validators: [this.passwordValidator, this.passwordsMatchValidator]}
  );

  constructor(
    private readonly notifier: NotifierService,
    private isLoadingService: IsLoadingService,
    private userService: UserService,
    private globalVars: GlobalVars,
    private fb: FormBuilder,
    private profilePicChangeListener: ProfilePicChangeListener,
    private storageService: StorageService
  ) {

  }

  ngOnInit(): void {
    this.userId = this.storageService.getUser().id;
    this.userPhotoUrl = this.globalVars.getApiUrl() + '/users/photo/' + this.userId;
  }

  private passwordsMatchValidator(formGroup: FormGroup): void {
    if (formGroup.controls.confirmPassword.value) {
      if (
        formGroup.controls.password.value !==
        formGroup.controls.confirmPassword.value
      ) {
        formGroup.controls.confirmPassword.setErrors({mismatch: true});
      } else {
        formGroup.controls.confirmPassword.setErrors(null);
      }
    }
  }

  private passwordValidator(formGroup: FormGroup): void {
    if (formGroup.controls.password.value) {
      const regex: RegExp = new RegExp(
        '^(?=.*[0-9])' +
        '(?=.*[a-z])(?=.*[A-Z])' +
        '(?=.*[!@#$%^&+=])' +
        '(?=\\S+$).{8,20}$'
      );
      if (!regex.test(formGroup.controls.password.value)) {
        formGroup.controls.password.setErrors({invalidPass: true});
      } else {
        formGroup.controls.password.setErrors(null);
      }
    }
  }

  submit(): void {
    this.submitted = true;
    if (this.form.valid) {
      const curPassword = this.form.controls.curPassword.value;
      const newPassword = this.form.controls.password.value;
      this.isLoadingService.add();
      this.userService.updateUserPassword(this.userId, curPassword, newPassword).subscribe(
        data => {
          this.notifier.notify('success', 'Password successfully updated');
          this.clearForm();
          this.isLoadingService.remove();
        },
        err => {
          console.log(err);
          // this.clearForm()
          this.notifier.notify('error', 'Password updation failed');
          this.isLoadingService.remove();
        }
      );
    }
  }

  clearForm(): void {
    this.form.reset();
    this.submitted = false;
    this.form.controls.curPassword.setErrors(null);
    this.form.controls.password.setErrors(null);
    this.form.controls.confirmPassword.setErrors(null);
  }

  handleFiles(files: FileList): void {
    this.fileToUpload = files.item(0);
    console.log('fileToUpload: ' + this.fileToUpload.name);
    this.uploadFile();
  }

  uploadFile(): void {
    if (this.fileToUpload != null) {
      this.isLoadingService.add();
      console.log('call the service to upload the file.');
      const userPhotoSubmit = {
        id: this.userId, // put user ID here
        photo: this.fileToUpload
      };
      this.userService.uploadUserPhoto(userPhotoSubmit).subscribe(
        data => {
          this.userPhotoUrl =
            this.globalVars.getApiUrl() +
            '/users/photo/' +
            this.userId +
            '?' +
            Number(new Date()); // this trick is done to reload the img tag
          // after the image is uploaded.
          this.fileToUpload = null;
          this.notifier.notify(
            'success',
            'Profile picture successfully updated'
          );

          // emit this event to update profile pic on toolbar
          this.profilePicChangeListener.profilePicUpdated(true);

          this.isLoadingService.remove();
        },
        err => {
          this.notifier.notify('error', 'Profile picture updation failed');
          this.isLoadingService.remove();
        }
      );
    } else {
      console.log('no file selected to upload');
    }
  }
}
