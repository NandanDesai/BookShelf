import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AboutDialog, AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {SignupComponent} from './signup/signup.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {ReactiveFormsModule} from '@angular/forms';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {DashComponent} from './dash/dash.component';
import {MatBadgeModule} from '@angular/material/badge';
import {IsLoadingModule} from '@service-work/is-loading';
import {NotifierModule, NotifierOptions} from 'angular-notifier';
import {PDFViewerComponent} from './pdfviewer/pdfviewer.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatDividerModule} from '@angular/material/divider';
import {MatTooltipModule} from '@angular/material/tooltip';
import {ProfileViewComponent} from './profile-view/profile-view.component';
import {MatListModule} from '@angular/material/list';
import {UserPhotoUploadComponent} from './user-edit/user-edit.component';
import {AddBookDialog, AdminDashComponent, DeleteBookDialog, EditUserDialog} from './admin-dash/admin-dash.component';
import {MatTableModule} from '@angular/material/table';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AuthInterceptor} from './_misc/auth.interceptor';
import {SharedModule} from './shared.module';
import {AuthGuard} from './_misc/auth.guard';
import {RoleGuard} from './_misc/role.guard';
import {LoggedInGuard} from './_misc/loggedin.guard';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';

const customNotifierOptions: NotifierOptions = {
  position: {
    horizontal: {
      position: 'right',
      distance: 12
    },
    vertical: {
      position: 'top',
      distance: 80,
      gap: 10
    }
  },
  theme: 'material',
  behaviour: {
    autoHide: 4000,
    onClick: 'hide',
    onMouseover: 'pauseAutoHide',
    showDismissButton: true,
    stacking: 4
  },
  animations: {
    enabled: true,
    show: {
      preset: 'slide',
      speed: 300,
      easing: 'ease'
    },
    hide: {
      preset: 'fade',
      speed: 300,
      easing: 'ease',
      offset: 50
    },
    shift: {
      speed: 300,
      easing: 'ease'
    },
    overlap: 150
  }
};


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    DashComponent,
    PDFViewerComponent,
    ProfileViewComponent,
    UserPhotoUploadComponent,
    AdminDashComponent,
    EditUserDialog,
    AddBookDialog,
    DeleteBookDialog,
    AboutDialog
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule.forRoot([
      {
        path: '',
        component: LoginComponent,
        canActivate: [LoggedInGuard]
      },
      {
        path: 'login',
        component: LoginComponent,
        data: {animationState: 'login'},
        canActivate: [LoggedInGuard]
      },
      {
        path: 'signup',
        component: SignupComponent,
        data: {animationState: 'signup'},
        canActivate: [LoggedInGuard]
      },
      {
        path: 'dash',
        component: DashComponent,
        data: {animationState: 'dash'},
        canActivate: [AuthGuard]
      },
      {
        path: 'pdf',
        component: PDFViewerComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'profile',
        component: ProfileViewComponent,
        data: {animationState: 'profile'},
        canActivate: [AuthGuard]
      },
      {
        path: 'useredit',
        component: UserPhotoUploadComponent,
        data: {animationState: 'useredit'},
        canActivate: [AuthGuard]
      },
      {
        path: 'admindash',
        component: AdminDashComponent,
        data: {animationState: 'admindash'},
        canActivate: [AuthGuard, RoleGuard]
      },
      {
        path: '**',
        redirectTo: ''
      },
    ], { useHash: true }),
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    ReactiveFormsModule,
    MatProgressBarModule,
    MatToolbarModule,
    MatGridListModule,
    MatMenuModule,
    MatIconModule,
    MatBadgeModule,
    IsLoadingModule,
    NotifierModule.withConfig(customNotifierOptions),
    PdfViewerModule,
    MatSidenavModule,
    MatDividerModule,
    MatTooltipModule,
    MatListModule,
    MatTableModule,
    MatDialogModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    SharedModule,
    MatSlideToggleModule
  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
