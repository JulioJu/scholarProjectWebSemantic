package fr.uga.julioju.jhipster.SempicRest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.RDFStoreModel;
import fr.uga.julioju.sempic.ResponseQuery;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

/**
 * REST controller for managing RDFStore.
 */
@RestController
@RequestMapping("/api")
public class SempicRest {

    private final Logger log = LoggerFactory.getLogger(SempicRest.class);

    /**
     * GET  //rdfquery_listsubclassof/:classQuery : get subclasses of classQuery
     *
     * @return the response with status 200 (OK) and the result of rdfquery in body
     *  or response with status 404 if ":classQuery" doesn't exist
     */
    @GetMapping("/rdfquery_listsubclassof/{classQuery}")
    public ResponseEntity<ResponseQuery>
    getRdfQuery(@PathVariable String classQuery) {

        log.debug("REST request to get subclass of:", classQuery);

        RDFStore rdfStore = new RDFStore();

        ArrayList<String> results = new ArrayList<String>();

        List<Resource> classes =
            rdfStore.listSubClassesOf(
                    // Note: we could use directly string uri, but it becomes
                    // vulnerable to SQL injection
                    ModelFactory.createDefaultModel().createResource(
                        SempicOnto.NS + classQuery
                    ));
        classes.forEach(c -> { results.add(c.toString()); });

        return ResponseEntity.ok()
            .body(new ResponseQuery(results));
    }

    /**
     * GET  /rdfStores : get a rdfStores.
     *
     * @return the response with status 200 (OK) and the list of rdfStores in body
     */
    @GetMapping("/rdfstore")
    public ResponseEntity<RDFStoreModel> getRdfStore() {
        log.debug("REST request to get RestStore");

        System.out.println("\n\n\nStart of app\n————————————\n\n");


        RDFStore rdfStore = new RDFStore();

        // ————————————————————————————
        System.out.println("\n\nStart of create triples\n————————————");

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
        System.out.println("End of create triples\n————————————\n\n");
        // ————————————————————————————

        rdfStore.saveModel(model);


        //rdfStore.deleteModel(model);
        //rdfStore.cnx.load(model);

        //rdfStore.deleteModel(model);
        //rdfStore.readPhoto(1).getModel().write(System.out,"turtle");
        // print the graph on the standard output
        //photoResource.getModel().write(System.out);

        System.out.println("\n\n\nEnd of app\n————————————\n\n");

        return ResponseEntity.ok()
            .body(new RDFStoreModel("bbbb"));
    }

}
