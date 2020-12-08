import {Injectable} from '@angular/core';
import {GlobalVars} from '../global.vars';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../_models/user';


@Injectable({
  providedIn: 'root'
})
export class AdminDashService {

  httpGetAndDeleteOptions = {
    headers: new HttpHeaders({Accept: 'application/json'})
  };

  httpPostAndPatchOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(private http: HttpClient, private globalVars: GlobalVars) {
  }

  getAllBooks(): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/books';
    console.log('API URL: ' + url);
    return this.http.get(url, this.httpGetAndDeleteOptions);
  }

  getAllUsers(): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/users';
    console.log('API URL: ' + url);
    return this.http.get(url, this.httpGetAndDeleteOptions);
  }

  deleteBook(bookId: number): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/books/' + bookId;
    console.log('API URL: ' + url);
    return this.http.delete(url, this.httpGetAndDeleteOptions);
  }

  addBook(bookSubmit): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/books';
    const formData: FormData = new FormData();
    formData.append('title', bookSubmit.title);
    formData.append('desc', bookSubmit.desc);
    formData.append('role', bookSubmit.role);
    formData.append('pdfFile', bookSubmit.pdfFile, bookSubmit.pdfFile.name);
    formData.append('photo', bookSubmit.photo, bookSubmit.photo.name);
    const httpOptions = {
      // headers: new HttpHeaders({ 'Content-Type': 'multipart/form-data', 'Accept': 'application/json' })
      headers: new HttpHeaders({Accept: 'application/json'})
    };
    console.log('API URL: ' + url);
    return this.http.post(url, formData, httpOptions);
  }

  // update role
  updateUser(user: User): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/users/role';
    console.log('API URL: ' + url);
    const payload = {
      id: user.id,
      role: user.role
    };
    return this.http.patch(url, payload, this.httpPostAndPatchOptions);
  }

}
