package fr.uga.julioju.sempic.entities;

public class AbstractRDF  {

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
