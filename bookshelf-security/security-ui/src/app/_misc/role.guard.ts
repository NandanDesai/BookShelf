import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {StorageService} from './storage.service';
import {GlobalVars} from '../global.vars';

// this guards the AdminDash component from non-admin users
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router,
              private storageService: StorageService,
              private globalVars: GlobalVars) {}
  canActivate(): boolean {
    if (this.globalVars.getApiUrl() === '/secure'){
      if (this.storageService.getUser().role === 'Admin'){
        console.log('role guard returning true');
        return true;
      }
    }else {
      if (this.storageService.getToken() != null) {
        if (this.storageService.getPayload().role === 'Admin') {
          console.log('role guard returning true');
          return true;
        }
      }
    }
    console.log('role guard returning false');
    this.router.navigate(['dash']);
    return false;
  }
}
