import {Injectable} from '@angular/core';
import {GlobalVars} from '../global.vars';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class DashService {

  httpOptions = {
    headers: new HttpHeaders({Accept: 'application/json'})
  };

  constructor(private http: HttpClient, private globalVars: GlobalVars) {
  }

  search(keyword: string): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/books/search';
    console.log('search service for keyword: ' + keyword);
    console.log('API URL: ' + url);
    this.httpOptions['params'] = {q: keyword};
    return this.http.get(url, this.httpOptions);
  }

  loadDash(): Observable<any> {
    const url = this.globalVars.getApiUrl() + '/books';
    console.log('API URL: ' + url);
    return this.http.get(url, this.httpOptions);
  }
}
