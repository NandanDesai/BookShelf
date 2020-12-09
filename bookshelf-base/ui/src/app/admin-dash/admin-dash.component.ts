import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {User} from '../_models/user';
import {Book} from '../_models/book';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, Validators} from '@angular/forms';
import {AdminDashService} from '../_misc/admindash.service';
import {NotifierService} from 'angular-notifier';
import {IsLoadingService} from '@service-work/is-loading';
import {GlobalVars} from '../global.vars';
import {MatTable} from '@angular/material/table';


@Component({
  selector: 'app-admin-dash',
  templateUrl: './admin-dash.component.html',
  styleUrls: ['./admin-dash.component.css', ],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class AdminDashComponent implements OnInit {

  @ViewChild(MatTable) table: MatTable<any>;

  /* List of Books table */

  booksDataSource: Book[] = [];
  booksDisplayedColumns: string[] = ['title', 'role', 'delete'];
  expandedBook: Book | null; // related to Book list table

  /* List of Users table */
  usersDataSource: User[] = [];
  usersDisplayedColumns: string[] = ['fullName', 'email', 'role', 'lastSeen', 'action'];

  // when del button is clicked, the rowClick() method is called twice.
  // to prevent the row from expanding and the opening of the dialog simultaneously,
  // im using ths extra delProcess variable.
  delProcess = false;

  constructor(private dialog: MatDialog,
              private adminDashService: AdminDashService,
              public isLoadingService: IsLoadingService,
              private notifier: NotifierService,
              public globalVars: GlobalVars) {
  }

  ngOnInit(): void {
    this.loadBooks();
    this.loadUsers();
  }

  loadBooks(): void {
    this.isLoadingService.add();
    this.adminDashService.getAllBooks().subscribe(
      data => {
        console.log(data);
        this.booksDataSource = Array.from(data.payload);
        this.isLoadingService.remove();
      },
      err => {
        console.log(err);
        this.notifier.notify('error', 'Failed to load the books.');
        this.isLoadingService.remove();
      }
    );
  }


  loadUsers(): void {
    this.isLoadingService.add();
    this.adminDashService.getAllUsers().subscribe(
      data => {
        console.log(data);
        this.usersDataSource = Array.from(data.payload);
        this.isLoadingService.remove();
      },
      err => {
        console.log(err);
        this.notifier.notify('error', 'Failed to load the users.');
        this.isLoadingService.remove();
      }
    );
  }

  editUser(user: User): void {
    console.log(user);
    const dialogRef = this.dialog.open(EditUserDialog, {
      width: '250px',
      data: user
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoadingService.add();

        this.adminDashService.updateUser(result).subscribe(
          data => {
            user.role = result.role;
            this.notifier.notify('success', 'User role successfully updated!');
            this.isLoadingService.remove();
          },
          err => {
            console.log(err);
            this.notifier.notify('error', 'User role updation failed');
            this.isLoadingService.remove();
          }
        );
      } else {
        console.log('User role update rejected by the user');
      }
    });
  }

  addBook(): void {
    const dialogRef = this.dialog.open(AddBookDialog, {
      width: '450px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoadingService.add();

        this.adminDashService.addBook(result).subscribe(
          data => {
            const bookResponse: Book = data.payload;
            this.booksDataSource.push(bookResponse);
            this.table.renderRows();
            this.notifier.notify('success', 'Book successfully added!');
            this.isLoadingService.remove();
          },
          err => {
            console.log(err);
            this.notifier.notify('error', 'Failed to upload the book');
            this.isLoadingService.remove();
          }
        );
      } else {
        console.log('Book add rejected by the user');
      }
    });

  }

  rowClick(book: Book, delBtnClicked: boolean): void {
    if (delBtnClicked) {
      console.log('del button clicked');
      this.delProcess = true;
      this.deleteBook(book);
    } else {
      if (!this.delProcess) {
        console.log('row clicked but not the del button');
        console.log('book: ' + book);
        this.expandedBook = this.expandedBook === book ? null : book;
      }
      this.delProcess = false;
    }
  }

  deleteBook(book: Book): void {
    console.log('calling for book deletion');
    console.log(book);
    const dialogRef = this.dialog.open(DeleteBookDialog, {
      width: '350px',
      data: book
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.isLoadingService.add();
        console.log('Deletion confirmed by the user');
        console.log(result);
        const bookResult: Book = result;
        this.adminDashService.deleteBook(bookResult.id).subscribe(
          data => {
            console.log(data);
            const index: number = this.booksDataSource.indexOf(bookResult, 0);
            if (index > -1) {
              this.booksDataSource.splice(index, 1);
            }
            console.log('booksDataSource details below');
            console.log(this.booksDataSource);
            this.table.renderRows();
            this.isLoadingService.remove();
            this.notifier.notify('success', 'Book successfully deleted');
          },
          err => {
            console.log(err);
            this.isLoadingService.remove();
            this.notifier.notify('error', 'Failed to delete the book');
          }
        );
      } else {
        console.log('Deletion rejected by the user');
      }
    });
  }
}


@Component({
  selector: 'edit-user-dialog',
  templateUrl: 'edit-user-dialog.html',
})
export class EditUserDialog {

  form = this.fb.group({
    role: [this.user.role]
  });

  roleList: string[] = ['Free', 'Basic', 'Premium', 'Admin'];

  constructor(
    public dialogRef: MatDialogRef<EditUserDialog>,
    @Inject(MAT_DIALOG_DATA) public user: User,
    private fb: FormBuilder, ) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onOkayClick(): void {
    const newRole = this.form.controls.role.value;
    const modifiedUser: User = {
      fullName: this.user.fullName,
      email: this.user.email,
      lastLogin: this.user.lastLogin,
      role: newRole,
      id: this.user.id
    };
    this.dialogRef.close(modifiedUser);
  }

}


@Component({
  selector: 'add-book-dialog',
  templateUrl: 'add-book-dialog.html',
})
export class AddBookDialog {

  submitted = false;

  coverPhotoToUpload: File = null;
  bookPdfToUpload: File = null;

  form = this.fb.group({
    title: [null, Validators.required],
    desc: [null, Validators.required],
    role: [null, Validators.required]
  });

  roleList: string[] = ['Free', 'Basic', 'Premium', 'Admin'];

  constructor(
    public dialogRef: MatDialogRef<AddBookDialog>,
    private fb: FormBuilder) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onOkayClick(): void {
    this.submitted = true;
    if (this.form.valid && this.coverPhotoToUpload != null && this.bookPdfToUpload != null) {

      const bookAddRequest = {
        title: this.form.controls.title.value,
        desc: this.form.controls.desc.value,
        role: this.form.controls.role.value,
        photo: this.coverPhotoToUpload,
        pdfFile: this.bookPdfToUpload
      };

      this.dialogRef.close(bookAddRequest);


    } else {
      console.log('form invalid');
    }
  }

  onCoverPhotoSelect(files: FileList): void {
    this.coverPhotoToUpload = files.item(0);
    console.log('coverPhotoToUpload: ' + this.coverPhotoToUpload.name);
  }

  onBookPdfSelect(files: FileList): void {
    this.bookPdfToUpload = files.item(0);
    console.log('bookPdfToUpload: ' + this.bookPdfToUpload.name);
  }
}

@Component({
  selector: 'del-book-dialog',
  templateUrl: 'del-book-dialog.html',
})
export class DeleteBookDialog {
  constructor(
    public dialogRef: MatDialogRef<DeleteBookDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Book,
    private fb: FormBuilder, ) {
  }

  onClick(): void {
    this.dialogRef.close();
  }

}
