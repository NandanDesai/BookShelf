import {Injectable} from '@angular/core';

import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfilePicChangeListener {
  private profilePicChanged = new BehaviorSubject(false);
  public profilePicChanged$ = this.profilePicChanged.asObservable();

  profilePicUpdated(change: boolean): void {
    this.profilePicChanged.next(change);
  }
}
