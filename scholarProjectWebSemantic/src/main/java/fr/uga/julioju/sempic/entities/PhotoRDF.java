package fr.uga.julioju.sempic.entities;

import javax.validation.constraints.NotNull;

public class PhotoRDF extends AbstractRDF {

    @NotNull
    private long albumId;

    private PhotoDepictionAnonRDF[] depiction;

    // Needed for Jackson if an other constructor is defined
    public PhotoRDF() {
    }

    public PhotoRDF(
            long id,
            long albumId,
            PhotoDepictionAnonRDF[] depiction
            ) {
        super(id);
        this.albumId    = albumId;
        this.depiction  = depiction;
    }

    public long getAlbumId() {
        return albumId;
    }

    public PhotoDepictionAnonRDF[] getDepiction() {
        return depiction;
    }
}
