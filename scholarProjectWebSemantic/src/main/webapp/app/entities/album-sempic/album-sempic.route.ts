import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AlbumSempic } from 'app/shared/model/album-sempic.model';
import { AlbumSempicService } from './album-sempic.service';
import { AlbumSempicComponent } from './album-sempic.component';
import { AlbumSempicDetailComponent } from './album-sempic-detail.component';
import { AlbumSempicUpdateComponent } from './album-sempic-update.component';
import { AlbumSempicDeletePopupComponent } from './album-sempic-delete-dialog.component';
import { IAlbumSempic } from 'app/shared/model/album-sempic.model';

@Injectable({ providedIn: 'root' })
export class AlbumSempicResolve implements Resolve<IAlbumSempic> {
  constructor(private service: AlbumSempicService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IAlbumSempic> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<AlbumSempic>) => response.ok),
        map((album: HttpResponse<AlbumSempic>) => album.body)
      );
    }
    return of(new AlbumSempic());
  }
}

export const albumRoute: Routes = [
  {
    path: '',
    component: AlbumSempicComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Albums'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AlbumSempicDetailComponent,
    resolve: {
      album: AlbumSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Albums'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AlbumSempicUpdateComponent,
    resolve: {
      album: AlbumSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Albums'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AlbumSempicUpdateComponent,
    resolve: {
      album: AlbumSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Albums'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const albumPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: AlbumSempicDeletePopupComponent,
    resolve: {
      album: AlbumSempicResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Albums'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
