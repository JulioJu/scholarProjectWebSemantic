package fr.uga.julioju.sempic.entities;

import javax.validation.constraints.NotNull;

/** Anonyme, therefore don't extend {@link AbstractRDF} */
public class PhotoDepictionAnonRDF  {

    @NotNull
    private String depiction;

    @NotNull
    private String[] literals;

    public PhotoDepictionAnonRDF() {
    }

    public PhotoDepictionAnonRDF(
        String depiction,
        String[] literals
    ) {
        this.depiction = depiction;
        this.literals  = literals;
    }

    public String getDepiction() {
        return depiction;
    }

    public String[] getLiterals() {
        return literals;
    }
}
