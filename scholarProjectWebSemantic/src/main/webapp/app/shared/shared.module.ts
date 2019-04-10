import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  ScholarProjectWebSemanticSharedLibsModule,
  ScholarProjectWebSemanticSharedCommonModule,
  JhiLoginModalComponent,
  HasAnyAuthorityDirective
} from './';

@NgModule({
  imports: [ScholarProjectWebSemanticSharedLibsModule, ScholarProjectWebSemanticSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [ScholarProjectWebSemanticSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ScholarProjectWebSemanticSharedModule {
  static forRoot() {
    return {
      ngModule: ScholarProjectWebSemanticSharedModule
    };
  }
}
