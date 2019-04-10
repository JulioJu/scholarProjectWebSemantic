package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {

        System.out.println("Start of app");

        RDFStore rdfStore = new RDFStore();

        // ————————————————————————————
        System.out.println("Coucou 0");

        Resource photoResource = rdfStore.createPhoto(1, 1, 1);
        Model model = ModelFactory.createDefaultModel();

        // animalResource
        Resource animalResource = model.createResource(SempicOnto.Animal);
        animalResource.addLiteral(RDFS.label, "Médor");
        model.add(photoResource, SempicOnto.depicts, animalResource);

        // Print
        model.write(System.out, "turtle");
        System.out.println("Coucou 1");

        // personResource
        Resource personResource = model.createResource(SempicOnto.Person);
        personResource.addLiteral(RDFS.label, "Charlemagne");
        model.add(photoResource, SempicOnto.depicts, personResource);

        // Print
        model.write(System.out, "turtle");
        System.out.println("Coucou 2");
        // ————————————————————————————

        rdfStore.saveModel(model);

        //rdfStore.deleteModel(model);
        //rdfStore.cnx.load(model);
        List<Resource> classes = rdfStore.listSubClassesOf(SempicOnto.Depiction);
        classes.forEach(c -> {System.out.println(c);});

        List<Resource> instances = rdfStore.createAnonInstances(classes);
        instances.forEach(i -> {
            System.out.println(i.getProperty(RDFS.label));
        });

        //rdfStore.deleteModel(model);
        //rdfStore.readPhoto(1).getModel().write(System.out,"turtle");
        // print the graph on the standard output
        //photoResource.getModel().write(System.out);
    }
}
