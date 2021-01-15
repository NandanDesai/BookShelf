import {Component, OnInit} from '@angular/core';
import {IsLoadingService} from '@service-work/is-loading';
import {Book} from '../_models/book';
import {FormBuilder, Validators} from '@angular/forms';
import {DashService} from '../_misc/dash.service';
import {NotifierService} from 'angular-notifier';
import {GlobalVars} from '../global.vars';

@Component({
  selector: 'app-dash',
  templateUrl: './dash.component.html',
  styleUrls: ['./dash.component.css']
})
export class DashComponent implements OnInit {

  books: Book[] = [];

  form = this.fb.group({
    search: [null, Validators.required]
  });

  searchSubmitted = false;
  searchKeyword: string = null;

  constructor(
    public isLoadingService: IsLoadingService, // public because its used in html template
    private fb: FormBuilder,
    private dashService: DashService,
    private notifier: NotifierService,
    public globalVars: GlobalVars
  ) {
  }

  ngOnInit(): void {
    console.log('ngOnInit() called');
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.isLoadingService.add();
    // add 800 ms delay in sending the request
    // this is for a smooth progress bar display
    // otherwise it was hurting my eyes to see sudden bright flashes of progress bar
    setTimeout(() => {
      this.dashService.loadDash().subscribe(
        data => {
          console.log(data);
          this.books = Array.from(data.payload);
          this.isLoadingService.remove();
        },
        err => {
          console.log(err);
          this.notifier.notify('error', 'Failed to load the dashboard.');
          this.isLoadingService.remove();
        }
      );
    }, 800);
  }

  searchSubmit(): void {
    this.searchKeyword = this.form.controls.search.value;
    if (this.form.valid && this.searchKeyword != null) {
      this.searchSubmitted = true;
      console.log('search keyword: ' + this.searchKeyword);
      this.books = [];
      this.isLoadingService.add();
      setTimeout(() => {
        this.dashService.search(this.searchKeyword).subscribe(
          data => {
            console.log(data);
            this.books = Array.from(data.payload);
            this.isLoadingService.remove();
          },
          err => {
            console.log(err);
            this.notifier.notify('error', 'Failed to search.');
            this.isLoadingService.remove();
          }
        );
      }, 800);

    }
  }

  clearClicked(): void {
    console.log('clear clicked');
    this.searchSubmitted = false;
    this.searchKeyword = null;
    this.form.reset();
    this.form.controls.search.setErrors(null);
    this.loadDashboard();
  }
}
