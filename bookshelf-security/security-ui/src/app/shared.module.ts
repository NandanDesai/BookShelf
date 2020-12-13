import {NgModule} from '@angular/core';
import {AuthImagePipe} from './_misc/authimage.pipe';

@NgModule({
  declarations: [
    AuthImagePipe
  ],
  exports: [
    AuthImagePipe
  ]
})
export class SharedModule {
}
