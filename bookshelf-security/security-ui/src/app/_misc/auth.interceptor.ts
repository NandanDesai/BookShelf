import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {StorageService} from './storage.service';
import {Observable} from 'rxjs';
import {GlobalVars} from '../global.vars';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private storageService: StorageService, private globalVars: GlobalVars) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // if (this.globalVars.getApiUrl() !== '/secure') {
    //   const token = this.storageService.getToken();
    //   if (token) {
    //     request = request.clone({
    //       setHeaders: {
    //         Authorization: `Bearer ${token}`
    //       }
    //     });
    //   }
    // }
    return next.handle(request);
  }
}
