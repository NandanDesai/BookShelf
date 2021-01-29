import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {IsLoadingService} from '@service-work/is-loading';
import {User} from '../_models/user';
import {UserService} from '../_misc/user.service';
import {StorageService} from '../_misc/storage.service';
import {NotifierService} from 'angular-notifier';
import {GlobalVars} from '../global.vars';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {
  user: User = null;
  userPhotoUrl = '';

  constructor(
    private router: Router,
    private isLoadingService: IsLoadingService,
    private userService: UserService,
    private storageService: StorageService,
    private notifier: NotifierService,
    private globalVars: GlobalVars
  ) {
  }

  ngOnInit(): void {
    this.isLoadingService.add();
    this.user = this.storageService.getUser();
    this.isLoadingService.remove();
    this.userPhotoUrl =
      this.globalVars.getApiUrl() + '/users/photo/' + this.user.id;
  }

  editButtonCLicked(): void {
    this.router.navigate(['/useredit', {}]);
  }
}
