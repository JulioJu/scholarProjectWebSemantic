import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ScholarProjectWebSemanticSharedModule } from 'app/shared';
import {
  PhotoSempicComponent,
  PhotoSempicDetailComponent,
  PhotoSempicUpdateComponent,
  PhotoSempicDeletePopupComponent,
  PhotoSempicDeleteDialogComponent,
  photoRoute,
  photoPopupRoute
} from './';

const ENTITY_STATES = [...photoRoute, ...photoPopupRoute];

@NgModule({
  imports: [ScholarProjectWebSemanticSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    PhotoSempicComponent,
    PhotoSempicDetailComponent,
    PhotoSempicUpdateComponent,
    PhotoSempicDeleteDialogComponent,
    PhotoSempicDeletePopupComponent
  ],
  entryComponents: [PhotoSempicComponent, PhotoSempicUpdateComponent, PhotoSempicDeleteDialogComponent, PhotoSempicDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ScholarProjectWebSemanticPhotoSempicModule {}
