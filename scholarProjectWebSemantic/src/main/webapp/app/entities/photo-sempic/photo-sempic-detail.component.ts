import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IPhotoSempic } from 'app/shared/model/photo-sempic.model';

@Component({
  selector: 'jhi-photo-sempic-detail',
  templateUrl: './photo-sempic-detail.component.html'
})
export class PhotoSempicDetailComponent implements OnInit {
  photo: IPhotoSempic;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ photo }) => {
      this.photo = photo;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
