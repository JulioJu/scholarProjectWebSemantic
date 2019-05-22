package fr.uga.julioju.sempic.entities;

import javax.validation.constraints.NotNull;

public class AlbumRDF extends AbstractRDF {

    @NotNull
    private String ownerLogin;

    private long[] sharedWith;

    // Needed for Jackson if an other constructor is defined
    public AlbumRDF() {
    }

    public AlbumRDF(long id, String ownerLogin) {
        super(id);
        this.ownerLogin = ownerLogin;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public long[] getSharedWith() {
        return sharedWith;
    }

}
