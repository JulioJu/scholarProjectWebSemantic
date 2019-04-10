import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IPhotoSempic, PhotoSempic } from 'app/shared/model/photo-sempic.model';
import { PhotoSempicService } from './photo-sempic.service';
import { IAlbumSempic } from 'app/shared/model/album-sempic.model';
import { AlbumSempicService } from 'app/entities/album-sempic';

@Component({
  selector: 'jhi-photo-sempic-update',
  templateUrl: './photo-sempic-update.component.html'
})
export class PhotoSempicUpdateComponent implements OnInit {
  photo: IPhotoSempic;
  isSaving: boolean;

  albums: IAlbumSempic[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    finThumbnail: [null, [Validators.required]],
    finThumbnailContentType: [],
    album: [null, Validators.required]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected photoService: PhotoSempicService,
    protected albumService: AlbumSempicService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ photo }) => {
      this.updateForm(photo);
      this.photo = photo;
    });
    this.albumService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IAlbumSempic[]>) => mayBeOk.ok),
        map((response: HttpResponse<IAlbumSempic[]>) => response.body)
      )
      .subscribe((res: IAlbumSempic[]) => (this.albums = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(photo: IPhotoSempic) {
    this.editForm.patchValue({
      id: photo.id,
      name: photo.name,
      finThumbnail: photo.finThumbnail,
      finThumbnailContentType: photo.finThumbnailContentType,
      album: photo.album
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file = event.target.files[0];
        if (isImage && !/^image\//.test(file.type)) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      () => console.log('blob added'), // sucess
      this.onError
    );
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string) {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null
    });
    if (this.elementRef && idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const photo = this.createFromForm();
    if (photo.id !== undefined) {
      this.subscribeToSaveResponse(this.photoService.update(photo));
    } else {
      this.subscribeToSaveResponse(this.photoService.create(photo));
    }
  }

  private createFromForm(): IPhotoSempic {
    const entity = {
      ...new PhotoSempic(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      finThumbnailContentType: this.editForm.get(['finThumbnailContentType']).value,
      finThumbnail: this.editForm.get(['finThumbnail']).value,
      album: this.editForm.get(['album']).value
    };
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPhotoSempic>>) {
    result.subscribe((res: HttpResponse<IPhotoSempic>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackAlbumById(index: number, item: IAlbumSempic) {
    return item.id;
  }
}
