import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {TokenStorageService} from './tokenstore.service';

// if already logged-in user is trying to access /login or /signup, then
// this guard redirects the user to /dash
@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate {
  constructor(private router: Router,
              private tokenStore: TokenStorageService ) {}
  canActivate(): boolean {
    if (this.tokenStore.getToken() != null){
      if (Math.floor(Date.now() / 1000) < this.tokenStore.getPayload().exp) {
        this.router.navigate(['dash']);
        return false;
      }else{
        console.log('token is valid but expired. Redirecting to /login');
        console.log('current timestamp: ' + Math.floor(Date.now() / 1000));
        console.log('token expiry: ' + this.tokenStore.getPayload().exp);
      }
    }
    return true;
  }
}
