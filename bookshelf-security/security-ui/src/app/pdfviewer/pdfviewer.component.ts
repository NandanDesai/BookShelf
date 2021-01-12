import {AfterViewInit, Component, OnInit, Renderer2} from '@angular/core';
import {IsLoadingService} from '@service-work/is-loading';
import {NotifierService} from 'angular-notifier';
import {PDFDocumentProxy} from 'ng2-pdf-viewer';
import {Book} from '../_models/book';
import {GlobalVars} from '../global.vars';
import {StorageService} from '../_misc/storage.service';

@Component({
  selector: 'app-pdfviewer',
  templateUrl: './pdfviewer.component.html',
  styleUrls: ['./pdfviewer.component.css']
})
export class PDFViewerComponent implements OnInit, AfterViewInit {

  roleValue = {
    Free: 1,
    Basic: 2,
    Premium: 3,
    Admin: 4
  };

  pdfSrc = {};
  openSidenav = false;
  allowed = false; // 'false' if the book should not be visible to the user
  pageNumber = 1;
  totalPages = 2;

  book: Book = null;

  constructor(
    private isLoadingService: IsLoadingService,
    private readonly notifier: NotifierService,
    private renderer: Renderer2,
    public globalVars: GlobalVars,
    private tokenStore: StorageService
  ) {
  }

  ngOnInit(): void {
    this.isLoadingService.add();
    this.openSidenav = false;

    this.book = history.state.data;
    // doing this to retain data on page reload
    if (this.book != null) {
      window.sessionStorage.setItem(
        'currentBookRead',
        JSON.stringify(this.book)
      );
    } else {
      this.book = JSON.parse(window.sessionStorage.getItem('currentBookRead'));
    }


    let userRole = '';
    if (this.globalVars.getApiUrl() === '/secure'){
      userRole = this.tokenStore.getUser().role;
      this.pdfSrc = {
        url: this.globalVars.getApiUrl() + '/books/pdf/' + this.book.id
      };
    }else{
      userRole = this.tokenStore.getPayload().role;
      this.pdfSrc = {
        url: this.globalVars.getApiUrl() + '/books/pdf/' + this.book.id,
        httpHeaders: {Authorization: `Bearer ${this.tokenStore.getToken()}`}
      };
    }
    const bookRole = this.book.role;

    if (this.roleValue[userRole] >= this.roleValue[bookRole]) {
      this.allowed = true;
    } else {
      this.allowed = false;
    }

    console.log(this.book);
    console.log('pdfSrc: ' + this.pdfSrc);
  }

  ngAfterViewInit(): void {
    if (!this.allowed) {
      this.isLoadingService.remove();
      const notAllowedDisplayEl = document.getElementById('not-allowed-display');
      const bookDisplayEl = document.getElementById('book-display');
      this.renderer.setStyle(notAllowedDisplayEl, 'display', 'block');
      this.renderer.setStyle(bookDisplayEl, 'display', 'none');
      this.openSidenav = true;
      this.notifier.notify(
        'info',
        'You don\'t have permission to view this book'
      );
    } else {
      const bookDisplayEl = document.getElementById('book-display');
      this.renderer.setStyle(bookDisplayEl, 'display', 'block');
    }
    console.log('allowed: ' + this.allowed);
  }

  pdfLoadComplete(pdf: PDFDocumentProxy): void {
    console.log('num of pages: ' + pdf.numPages);
    this.totalPages = pdf.numPages;
    this.isLoadingService.remove();
    this.openSidenav = true;
    this.notifier.notify('success', 'Now reading: ' + this.book.title);
  }

  onError(event): void {
    this.isLoadingService.remove();
    this.notifier.notify('error', 'Sorry, failed to load the PDF');
    console.log(event);
  }

  nextPage(): void {
    if (this.pageNumber !== this.totalPages) {
      this.pageNumber++;
    }
  }

  previousPage(): void {
    if (this.pageNumber !== 1) {
      this.pageNumber--;
    }
  }
}
