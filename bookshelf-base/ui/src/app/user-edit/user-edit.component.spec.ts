import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserPhotoUploadComponent} from './user-edit.component';

describe('UserPhotoUploadComponent', () => {
  let component: UserPhotoUploadComponent;
  let fixture: ComponentFixture<UserPhotoUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserPhotoUploadComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPhotoUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
