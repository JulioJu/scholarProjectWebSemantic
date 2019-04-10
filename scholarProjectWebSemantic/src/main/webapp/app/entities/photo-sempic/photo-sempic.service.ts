import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPhotoSempic } from 'app/shared/model/photo-sempic.model';

type EntityResponseType = HttpResponse<IPhotoSempic>;
type EntityArrayResponseType = HttpResponse<IPhotoSempic[]>;

@Injectable({ providedIn: 'root' })
export class PhotoSempicService {
  public resourceUrl = SERVER_API_URL + 'api/photos';

  constructor(protected http: HttpClient) {}

  create(photo: IPhotoSempic): Observable<EntityResponseType> {
    return this.http.post<IPhotoSempic>(this.resourceUrl, photo, { observe: 'response' });
  }

  update(photo: IPhotoSempic): Observable<EntityResponseType> {
    return this.http.put<IPhotoSempic>(this.resourceUrl, photo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPhotoSempic>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPhotoSempic[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
