import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminDashComponent} from './admin-dash.component';

describe('AdminDashComponent', () => {
  let component: AdminDashComponent;
  let fixture: ComponentFixture<AdminDashComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminDashComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminDashComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
