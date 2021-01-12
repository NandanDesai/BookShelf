import {Injectable} from '@angular/core';
import {TokenPayload} from '../_models/token-payload';
import {User} from '../_models/user';

const TOKEN_KEY = 'auth-token';
const PAYLOAD_KEY = 'token-payload';
const USER_KEY = 'user';

const API_URL = 'apiUrl';
const TOOLBAR_STYLE = 'toolbarStyle';
const SLIDE_TOGGLE_STATE = 'slideToggleState';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() {
  }

  clearStore(): void {
    localStorage.clear();
  }

  public saveToken(token: string): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string {
    return localStorage.getItem(TOKEN_KEY);
  }

  public savePayload(payload: TokenPayload): void {
    localStorage.removeItem(PAYLOAD_KEY);
    localStorage.setItem(PAYLOAD_KEY, JSON.stringify(payload));
  }

  public getPayload(): TokenPayload{
    return JSON.parse(localStorage.getItem(PAYLOAD_KEY));
  }

  public saveUser(user: User): void{
    localStorage.removeItem(USER_KEY);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): User{
    return JSON.parse(localStorage.getItem(USER_KEY));
  }

  public saveApiUrl(apiUrl: string): void{
    localStorage.setItem(API_URL, apiUrl);
  }

  public getApiUrl(): string{
    return localStorage.getItem(API_URL);
  }

  public saveToolbarStyle(toolbarStyle: string): void{
    localStorage.setItem(TOOLBAR_STYLE, toolbarStyle);
  }

  public getToolbarStyle(): string{
    return localStorage.getItem(TOOLBAR_STYLE);
  }

  public saveSlideToggleState(state): void{
    localStorage.setItem(SLIDE_TOGGLE_STATE, state);
  }

  // tslint:disable-next-line:typedef
  public getSlideToggleState(){
    return localStorage.getItem(SLIDE_TOGGLE_STATE);
  }
}
