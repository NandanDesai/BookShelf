import {Injectable} from '@angular/core';


/*
* This class will be used in other versions of BookShelf. For BookShelf Base, this class
* doesn't really do anything. The apiUrl just holds '/base' value which will be set in
* onInit() of AppComponent.
* */
@Injectable({
  providedIn: 'root'
})
export class GlobalVars {
  private apiUrl = '';

  public getApiUrl(): string {
    return this.apiUrl;
  }

  public setApiUrlExtension(extension: string): void {
    this.apiUrl = '' + extension;
  }
}
