package fr.uga.julioju.jhipster.SempicRest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
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
     * {@code PUT  /photo} : Creates or Updates a photoRDF
     *
     * @param photoRDF the photoRDF to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoRDF,
     * or with status {@code 400 (Bad Request)} if the photoRDF is not valid,
     * or with status {@code 500 (Internal Server Error)} if the photoRDF couldn't be updated.
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/photo")
    public ResponseEntity<PhotoRDF> updatePhotoRDF(
            @Valid @RequestBody PhotoRDF photoRDF)
            throws UnsupportedEncodingException {
        log.debug("REST request to update PhotoRDF : {}", photoRDF);


        RDFStore rdfStore = new RDFStore();

        Resource photoResource = rdfStore.createPhoto(photoRDF.getPhotoId(),
                photoRDF.getAlbumId(), photoRDF.getOwnerId());

        // Delete photos before update, otherwise it appends
        rdfStore.deleteResource(photoResource);

        Model model = photoResource.getModel();


        // TODO use bag
        for (int depictionIndex = 0 ;
                depictionIndex < photoRDF.getDepiction().length ;
                depictionIndex++) {
            String depictionURI = SempicOnto.NS
                + photoRDF.getDepiction()[depictionIndex].getDepiction();
            Resource sempicOntoResource = model.createResource(depictionURI);
            rdfStore.testIfUriIsClass(sempicOntoResource.getURI());

            Resource descriptionResource = model.createResource();

            for (int literalsIndex = 0 ;
                    literalsIndex <
                        photoRDF.getDepiction()[depictionIndex]
                            .getLiterals().length ;
                    literalsIndex++
            ) {
                descriptionResource.addProperty(RDF.type, sempicOntoResource);
                descriptionResource.addLiteral(RDFS.label,
                        photoRDF.getDepiction()[depictionIndex]
                        .getLiterals()[literalsIndex]);
            }
            model.add(photoResource, SempicOnto.depicts, descriptionResource);
        }

        System.out.println("BELOW: MODEL BEFORE IT SAVED\n—————————————");
        model.write(System.out, "turtle");

        // print the graph on the standard output
        System.out.println("BELOW: PRINT RESOURCE\n—————————————");
        photoResource.getModel().write(System.out, "turtle");

        rdfStore.saveModel(model);
        System.out.println("BELOW: PRINT MODEL SAVED\n—————————————");
        rdfStore.readPhoto(photoRDF.getPhotoId(), true)
            .getModel().write(System.out, "turtle");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rdfStore.readPhoto(photoRDF.getPhotoId(), false)
            .getModel().write(baos, "n-triple");
        photoRDF.setTurtleRepresString(baos.toString("utf8"));

        return ResponseEntity.ok().body(photoRDF);
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
