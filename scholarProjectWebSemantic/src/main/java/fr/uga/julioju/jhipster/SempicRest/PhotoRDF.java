package fr.uga.julioju.jhipster.SempicRest;

public class PhotoRDF  {

    private long photoId;

    private long albumId;

    private long ownerId;

    private PhotoDepictionRDF[] depiction;

    // Needed for Jackson if an other constructor is defined
    public PhotoRDF() {
    }

    public PhotoRDF(
            long photoId,
            long albumId,
            long ownerId,
            PhotoDepictionRDF[] depiction
            ) {
           this.photoId             = photoId;
           this.albumId             = albumId;
           this.ownerId             = ownerId;
           this.depiction           = depiction;
    }

    public long getPhotoId() {
        return photoId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public PhotoDepictionRDF[] getDepiction() {
        return depiction;
    }
}
