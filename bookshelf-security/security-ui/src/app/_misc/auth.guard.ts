import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import {StorageService} from './storage.service';
import {GlobalVars} from '../global.vars';
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router,
              private storageService: StorageService,
              private globalVars: GlobalVars) {}
  canActivate(): boolean {
    console.log('authguard: apiUrl=' + this.globalVars.getApiUrl());
    if (this.storageService.getUser() != null){
        return true;
    }
    this.router.navigate(['login']);
    return false;
  }
}
