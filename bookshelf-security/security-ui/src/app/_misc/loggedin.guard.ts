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
    if (this.storageService.getUser() != null){
      this.router.navigate(['dash']);
      return false;
    }
    return true;
  }
}
