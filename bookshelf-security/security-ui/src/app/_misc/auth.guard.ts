import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {TokenStorageService} from './tokenstore.service';
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router,
              private tokenStore: TokenStorageService ) {}
  canActivate(): boolean {
    if (this.tokenStore.getToken() != null){
      if (Math.floor(Date.now() / 1000) < this.tokenStore.getPayload().exp) {
        return true;
      }else{
        console.log('token is valid but expired. Redirecting to /login');
        console.log('current timestamp: ' + Math.floor(Date.now() / 1000));
        console.log('token expiry: ' + this.tokenStore.getPayload().exp);
      }
    }
    this.router.navigate(['login']);
    return false;
  }
}
