import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAlbumSempic } from 'app/shared/model/album-sempic.model';

@Component({
  selector: 'jhi-album-sempic-detail',
  templateUrl: './album-sempic-detail.component.html'
})
export class AlbumSempicDetailComponent implements OnInit {
  album: IAlbumSempic;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ album }) => {
      this.album = album;
    });
  }

  previousState() {
    window.history.back();
  }
}
