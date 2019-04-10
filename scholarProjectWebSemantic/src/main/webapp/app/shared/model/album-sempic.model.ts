import { IPhotoSempic } from 'app/shared/model/photo-sempic.model';
import { IUser } from 'app/core/user/user.model';

export interface IAlbumSempic {
  id?: number;
  name?: string;
  photos?: IPhotoSempic[];
  user?: IUser;
}

export class AlbumSempic implements IAlbumSempic {
  constructor(public id?: number, public name?: string, public photos?: IPhotoSempic[], public user?: IUser) {}
}
