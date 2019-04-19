package fr.uga.julioju.jhipster.SempicRest;

public class PhotoRDF  {

    private long photoId;

    private long albumId;

    private long ownerId;

    private PhotoDepictionRDF[] depiction;

    private String turtleRepresString;

    public long getPhotoId() {
        return photoId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getTurtleRepresString() {
        return turtleRepresString;
    }

    public void setTurtleRepresString(String turtleRepresString) {
        this.turtleRepresString = turtleRepresString;
    }

    public PhotoDepictionRDF[] getDepiction() {
        return depiction;
    }
}
