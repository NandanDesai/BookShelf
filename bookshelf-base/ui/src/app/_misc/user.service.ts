import {Injectable} from '@angular/core';
import {GlobalVars} from '../global.vars';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient, private globalVars: GlobalVars) {
  }

  uploadUserPhoto(userPhotoSubmit): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/users/photo';
    const formData: FormData = new FormData();
    formData.append('id', userPhotoSubmit.id);
    formData.append('photo', userPhotoSubmit.photo, userPhotoSubmit.photo.name);
    const httpOptions = {
      headers: new HttpHeaders({Accept: 'application/json'})
    };
    console.log('API URL: ' + url);
    return this.http.post(url, formData, httpOptions);
  }

  getUser(userId: number): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/users/' + userId;
    console.log('API URL: ' + url);
    const httpOptions = {
      headers: new HttpHeaders({
        Accept: 'application/json'
      })
    };
    return this.http.get(url, httpOptions);
  }

  updateUserPassword(userId: number, curPassword: string, newPassword: string): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/users/pass';
    console.log('API URL: ' + url);
    const httpOptions = {
      headers: new HttpHeaders({
        Accept: 'application/json',
        'Content-Type': 'application/json'
      })
    };
    const payload = {
      id: userId,
      curPassword: curPassword,
      newPassword: newPassword
    };
    return this.http.patch(url, payload, httpOptions);
  }
}
