import {Component, OnInit} from '@angular/core';
import {GlobalVars} from './global.vars';
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet} from '@angular/router';
import {IsLoadingService} from '@service-work/is-loading';
import {Observable} from 'rxjs';
import {filter} from 'rxjs/operators';
import {routeTransitionAnimations} from './route-transition-animations';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {TokenStorageService} from './_misc/tokenstore.service';
import {TokenPayload} from './_models/token-payload';
import {NotifierService} from 'angular-notifier';
import {ProfilePicChangeListener} from './_misc/profilepic-change-listener.service';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [routeTransitionAnimations]
})
export class AppComponent implements OnInit {
  isLoading: Observable<boolean>;
  showMenu = false;
  toolbarStyle = 'color: white; background: #E10712';

  role = 'undefined';
  userId = -1;
  userPhotoUrl = '';

  constructor(
    private isLoadingService: IsLoadingService,
    private globalVars: GlobalVars,
    private router: Router,
    private dialog: MatDialog,
    private tokenService: TokenStorageService,
    private notifier: NotifierService,
    private profilePicChangeListener: ProfilePicChangeListener
  ) {
  }

  ngOnInit(): void {
    this.globalVars.setApiUrlExtension('/base');
    console.log('DEFAULT API URL: ' + this.globalVars.getApiUrl());

    // Note, because `IsLoadingService#isLoading$()` returns
    // a new observable each time it is called, it shouldn't
    // be called directly inside a component template.
    this.isLoading = this.isLoadingService.isLoading$();

    // when profile pic is updated in user-edit.component, this will be triggered from there.
    this.profilePicChangeListener.profilePicChanged$.subscribe(
      changed => {
        if (changed) {
          console.log('profile pic updated? ' + changed);
          this.userPhotoUrl =
            this.globalVars.getApiUrl() +
            '/users/photo/' +
            this.userId +
            '?' +
            Number(new Date()); // this trick is done to reload the img tag
          // after the image is uploaded.
        }
      }
    );

    this.router.events
      .pipe(
        filter(
          event =>
            event instanceof NavigationStart ||
            event instanceof NavigationEnd ||
            event instanceof NavigationCancel ||
            event instanceof NavigationError
        )
      )
      .subscribe(event => {
        // If it's the start of navigation, `add()` a loading indicator
        if (event instanceof NavigationStart) {
          console.log('navigation started. Showing progress bar.');
          this.isLoadingService.add();
          return;
        }

        // Else navigation has ended, so `remove()` a loading indicator
        console.log('navigation ended. Removing progress bar.');
        this.isLoadingService.remove();
      });

    // this is to load pdf.worker.js from assets folder instead of CDN
    // I've set this according to what was given in the README page of the repository
    (window as any).pdfWorkerSrc = '/assets/pdf.worker.es5.v2.5.207.js';
  }

  public onRouterOutletActivate(event): void {
    // display toggle only if the component is login or signup. Otherwise no...
    console.log(this.router.url);
    if (
      this.router.url === '/' ||
      this.router.url === '/login' ||
      this.router.url === '/signup'
    ) {
      this.showMenu = false;
    } else {
      this.showMenu = true;

      // if the route is not /login or /signup or /, then load the user role and id from jwt token payload
      // this will be used to set the Role name and Profile Pic on the UI toolbar
      const tokenPayload: TokenPayload = this.tokenService.getPayload();
      if (tokenPayload) {
        this.role = tokenPayload.role;
        this.userId = tokenPayload.userId;
        this.userPhotoUrl =
          this.globalVars.getApiUrl() + '/users/photo/' + this.userId;
      }
    }
  }

  // this method is for animation purposes when the routes are changing.
  prepareRoute(outlet: RouterOutlet): void {
    return (
      outlet &&
      outlet.activatedRouteData &&
      outlet.activatedRouteData.animationState
    );
  }

  showProfile(): void {
    console.log('showProfile method called.');
    this.router.navigate(['/profile', {}]);
  }

  showAdminDash(): void {
    console.log('showAdminDash method called.');
    this.router.navigate(['/admindash', {}]);
  }

  logout(): void {
    console.log('logout method called');
    this.tokenService.clearStore();
    this.router.navigate(['/login', {}]);
    this.notifier.notify('success', 'Successfully logged out');
  }

  about(): void {
    const dialogRef = this.dialog.open(AboutDialog, {
      width: '450px'
    });
  }
}

@Component({
  selector: 'about-dialog',
  templateUrl: 'about-dialog.html',
})
export class AboutDialog {
  angularAppVersion = environment.version;
  serverVersion = environment.serverVersion;
  constructor(
    public dialogRef: MatDialogRef<AboutDialog>) {
  }
}
