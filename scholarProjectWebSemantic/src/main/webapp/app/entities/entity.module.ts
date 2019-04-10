import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'album-sempic',
        loadChildren: './album-sempic/album-sempic.module#ScholarProjectWebSemanticAlbumSempicModule'
      },
      {
        path: 'photo-sempic',
        loadChildren: './photo-sempic/photo-sempic.module#ScholarProjectWebSemanticPhotoSempicModule'
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ScholarProjectWebSemanticEntityModule {}
