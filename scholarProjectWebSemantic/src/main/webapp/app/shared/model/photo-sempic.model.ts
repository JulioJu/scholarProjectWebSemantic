import { IAlbumSempic } from 'app/shared/model/album-sempic.model';

export interface IPhotoSempic {
  id?: number;
  name?: string;
  finThumbnailContentType?: string;
  finThumbnail?: any;
  album?: IAlbumSempic;
}

export class PhotoSempic implements IPhotoSempic {
  constructor(
    public id?: number,
    public name?: string,
    public finThumbnailContentType?: string,
    public finThumbnail?: any,
    public album?: IAlbumSempic
  ) {}
}
