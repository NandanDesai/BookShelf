import {Injectable} from '@angular/core';
import {GlobalVars} from '../global.vars';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(private http: HttpClient, private globalVars: GlobalVars) {
  }

  login(credentials): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/login';
    console.log('email: ' + credentials.email);
    console.log('password: ' + credentials.password);
    console.log('API URL: ' + this.globalVars.getApiUrl());
    return this.http.post(url, {
      email: credentials.email,
      password: credentials.password
    }, this.httpOptions);
  }

  signup(user): Observable<any> {
    return this.http.post(this.globalVars.getApiUrl() + '/signup', {
      fullName: user.fullName,
      email: user.email,
      password: user.password
    }, this.httpOptions);
  }
}
