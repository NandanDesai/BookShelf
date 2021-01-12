import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {StorageService} from './storage.service';
import {GlobalVars} from "../global.vars";

// if already logged-in user is trying to access /login or /signup, then
// this guard redirects the user to /dash
@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate {
  constructor(private router: Router,
              private storageService: StorageService,
              private globalVars: GlobalVars) {}
  canActivate(): boolean {
    if (this.globalVars.getApiUrl() === '/secure'){
      if (this.storageService.getUser() != null){
        this.router.navigate(['dash']);
        return false;
      }
    }else {
      if (this.storageService.getToken() != null) {
        if (Math.floor(Date.now() / 1000) < this.storageService.getPayload().exp) {
          this.router.navigate(['dash']);
          return false;
        } else {
          console.log('token is valid but expired. Redirecting to /login');
          console.log('current timestamp: ' + Math.floor(Date.now() / 1000));
          console.log('token expiry: ' + this.storageService.getPayload().exp);
        }
      }
    }
    return true;
  }
}
