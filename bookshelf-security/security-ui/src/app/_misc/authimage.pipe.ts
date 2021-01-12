import {Pipe, PipeTransform} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {StorageService} from './storage.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {GlobalVars} from "../global.vars";

@Pipe({
  name: 'authimage'
})
export class AuthImagePipe implements PipeTransform {

  constructor(
    private http: HttpClient,
    private storageService: StorageService,
    private sanitizer: DomSanitizer,
    private globalVars: GlobalVars) {
  }

  async transform(src: string): Promise<SafeUrl> {
    let headers = null;
    if (this.globalVars.getApiUrl() !== '/secure') {
      const token = this.storageService.getToken();
      headers = new HttpHeaders({Authorization: `Bearer ${token}`});
    }
    try {
      let imageBlob = null;
      if (headers != null) {
        imageBlob = await this.http.get(src, {headers, responseType: 'blob'}).toPromise();
      } else {
        imageBlob = await this.http.get(src, {responseType: 'blob'}).toPromise();
      }
      return new Promise((resolve, reject) => {
        const urlCreator = window.URL;
        const imageUrl = urlCreator.createObjectURL(imageBlob);
        // im bypassing Angular default security check on Urls because I trust my imageUrl
        // if i dont bypass this, Angular will flag this as unsafe and throw an exception
        // because the object url will be in the format -- blob:http://localhost:4200/b33702cb-f7d6-454f-b5e2-e5785e0a0a9f
        // refer the following links to know more:
        // https://angular.io/guide/security#xss
        // https://angular.io/api/platform-browser/DomSanitizer
        const safeImageUrl: SafeUrl = this.sanitizer.bypassSecurityTrustUrl(imageUrl);
        resolve(safeImageUrl);
      });
    } catch {
      return '';
    }

  }

}
