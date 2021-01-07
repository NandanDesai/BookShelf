import {Injectable} from '@angular/core';

import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModeChangeListener {
  private modeChanged = new BehaviorSubject(false);
  public profilePicChanged$ = this.modeChanged.asObservable();

  // set true if secure mode activated. Else false.
  secureMode(secure: boolean): void {
    this.modeChanged.next(secure);
  }
}
