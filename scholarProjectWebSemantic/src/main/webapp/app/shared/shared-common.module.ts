import { NgModule } from '@angular/core';

import { ScholarProjectWebSemanticSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [ScholarProjectWebSemanticSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [ScholarProjectWebSemanticSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class ScholarProjectWebSemanticSharedCommonModule {}
