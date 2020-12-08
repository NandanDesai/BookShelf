import {Injectable} from '@angular/core';
import {TokenPayload} from '../_models/token-payload';

const TOKEN_KEY = 'auth-token';
const PAYLOAD_KEY = 'token-payload';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

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

  public getPayload(): TokenPayload {
    return JSON.parse(localStorage.getItem(PAYLOAD_KEY));
  }
}
