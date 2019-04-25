package fr.uga.julioju.jhipster.SempicRest;

public class PhotoDepictionRDF  {
    private String depiction;
    private String[] literals;

    public PhotoDepictionRDF() {
    }

    public PhotoDepictionRDF(
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
