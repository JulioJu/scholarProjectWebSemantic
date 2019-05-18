package fr.uga.julioju.sempic.entities;

public class AlbumRDF extends AbstractRDF {

    private long ownerId;

    private long[] sharedWith;

    // Needed for Jackson if an other constructor is defined
    public AlbumRDF() {
    }

    public AlbumRDF(long id, long ownerId) {
        super(id);
        this.ownerId = ownerId;
    }

    public long getUserId() {
        return ownerId;
    }

    public long[] getSharedWith() {
        return sharedWith;
    }

}
