import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PDFViewerComponent} from './pdfviewer.component';

describe('PDFViewerComponent', () => {
  let component: PDFViewerComponent;
  let fixture: ComponentFixture<PDFViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PDFViewerComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PDFViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
