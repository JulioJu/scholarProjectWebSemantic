import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { PhotoSempic } from 'app/shared/model/photo-sempic.model';
import { PhotoSempicService } from './photo-sempic.service';
import { PhotoSempicComponent } from './photo-sempic.component';
import { PhotoSempicDetailComponent } from './photo-sempic-detail.component';
import { PhotoSempicUpdateComponent } from './photo-sempic-update.component';
import { PhotoSempicDeletePopupComponent } from './photo-sempic-delete-dialog.component';
import { IPhotoSempic } from 'app/shared/model/photo-sempic.model';

@Injectable({ providedIn: 'root' })
export class PhotoSempicResolve implements Resolve<IPhotoSempic> {
  constructor(private service: PhotoSempicService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IPhotoSempic> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<PhotoSempic>) => response.ok),
        map((photo: HttpResponse<PhotoSempic>) => photo.body)
      );
    }
    return of(new PhotoSempic());
  }
}

export const photoRoute: Routes = [
  {
    path: '',
    component: PhotoSempicComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Photos'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PhotoSempicDetailComponent,
    resolve: {
      photo: PhotoSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Photos'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PhotoSempicUpdateComponent,
    resolve: {
      photo: PhotoSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Photos'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PhotoSempicUpdateComponent,
    resolve: {
      photo: PhotoSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Photos'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const photoPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: PhotoSempicDeletePopupComponent,
    resolve: {
      photo: PhotoSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Photos'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
