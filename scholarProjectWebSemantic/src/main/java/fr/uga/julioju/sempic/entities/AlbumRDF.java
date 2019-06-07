package fr.uga.julioju.sempic.entities;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class AlbumRDF extends AbstractRDF {

    @NotNull
    private String ownerLogin;

    @Nullable
    private String[] sharedWith;

    @NotNull
    private String title;

    // Needed for Jackson if an other constructor is defined
    public AlbumRDF() {
    }

    public AlbumRDF(long id, String title, String ownerLogin) {
        super(id);
        this.title = title;
        this.ownerLogin = ownerLogin;
    }

    public AlbumRDF(long id, String title, String ownerLogin, String[] sharedWithLogin) {
        super(id);
        this.title = title;
        this.ownerLogin = ownerLogin;
        this.sharedWith = sharedWithLogin;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public String[] getSharedWith() {
        return sharedWith;
    }

    public String getTitle() {
        return title;
    }
}
