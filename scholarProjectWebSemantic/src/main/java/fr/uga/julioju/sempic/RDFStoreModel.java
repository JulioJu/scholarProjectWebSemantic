package fr.uga.julioju.sempic;
import javax.json.bind.annotation.JsonbProperty;


public class RDFStoreModel {

    @JsonbProperty(/* value = "toto", */ nillable = true)
    public String coucou;

    public RDFStoreModel(String coucou) {
        this.coucou = coucou;
    }

}
