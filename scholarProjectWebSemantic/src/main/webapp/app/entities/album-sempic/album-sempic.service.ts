import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAlbumSempic } from 'app/shared/model/album-sempic.model';

type EntityResponseType = HttpResponse<IAlbumSempic>;
type EntityArrayResponseType = HttpResponse<IAlbumSempic[]>;

@Injectable({ providedIn: 'root' })
export class AlbumSempicService {
  public resourceUrl = SERVER_API_URL + 'api/albums';

  constructor(protected http: HttpClient) {}

  create(album: IAlbumSempic): Observable<EntityResponseType> {
    return this.http.post<IAlbumSempic>(this.resourceUrl, album, { observe: 'response' });
  }

  update(album: IAlbumSempic): Observable<EntityResponseType> {
    return this.http.put<IAlbumSempic>(this.resourceUrl, album, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAlbumSempic>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAlbumSempic[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
