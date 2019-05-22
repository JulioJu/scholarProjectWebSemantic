package fr.uga.julioju.sempic.entities;

import javax.validation.constraints.NotNull;

public class AbstractRDF  {

    @NotNull
    private long id;

    protected AbstractRDF() {

    }

    protected AbstractRDF(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
