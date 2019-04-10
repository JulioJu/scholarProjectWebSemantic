import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ScholarProjectWebSemanticSharedModule } from 'app/shared';
import {
  AlbumSempicComponent,
  AlbumSempicDetailComponent,
  AlbumSempicUpdateComponent,
  AlbumSempicDeletePopupComponent,
  AlbumSempicDeleteDialogComponent,
  albumRoute,
  albumPopupRoute
} from './';

const ENTITY_STATES = [...albumRoute, ...albumPopupRoute];

@NgModule({
  imports: [ScholarProjectWebSemanticSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    AlbumSempicComponent,
    AlbumSempicDetailComponent,
    AlbumSempicUpdateComponent,
    AlbumSempicDeleteDialogComponent,
    AlbumSempicDeletePopupComponent
  ],
  entryComponents: [AlbumSempicComponent, AlbumSempicUpdateComponent, AlbumSempicDeleteDialogComponent, AlbumSempicDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ScholarProjectWebSemanticAlbumSempicModule {}
