export class TokenPayload {
  public userId: number;
  public email: string;
  public role: string;
  public iat: number; // issued at
  public exp: number; // expiry date
  public iss: string; // issuer
  constructor() {
  }
}
