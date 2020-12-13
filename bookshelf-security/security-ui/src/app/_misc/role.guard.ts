import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {TokenStorageService} from './tokenstore.service';

// this guards the AdminDash component from non-admin users
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router,
              private tokenStore: TokenStorageService ) {}
  canActivate(): boolean {
    if (this.tokenStore.getToken() != null){
      if (this.tokenStore.getPayload().role === 'Admin') {
        console.log('role guard returning true');
        return true;
      }
    }
    console.log('role guard returning false');
    this.router.navigate(['dash']);
    return false;
  }
}
