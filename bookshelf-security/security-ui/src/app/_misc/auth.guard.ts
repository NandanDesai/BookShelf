import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {StorageService} from './storage.service';
import {GlobalVars} from "../global.vars";
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router,
              private storageService: StorageService,
              private globalVars: GlobalVars) {}
  canActivate(): boolean {
    console.log('authguard: apiUrl='+this.globalVars.getApiUrl());
    if (this.globalVars.getApiUrl() === '/secure'){
      if (this.storageService.getUser() != null){
        return true;
      }
    }else {
      if (this.storageService.getToken() != null) {
        if (Math.floor(Date.now() / 1000) < this.storageService.getPayload().exp) {
          return true;
        } else {
          console.log('token is valid but expired. Redirecting to /login');
          console.log('current timestamp: ' + Math.floor(Date.now() / 1000));
          console.log('token expiry: ' + this.storageService.getPayload().exp);
        }
      }
    }
    this.router.navigate(['login']);
    return false;
  }
}
