package fr.uga.julioju.jhipster.SempicRest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ResponseQuery;
import fr.uga.julioju.sempic.Exceptions.FusekiJenaQueryException;
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
     *      (ontology SempicOnto)
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
            rdfStore.listSubClassesOf(SempicOnto.NS + classQuery);
        classes.forEach(c -> { results.add(c.toString()); });
        log.debug("Subclasses: ", classes.toString());

        return ResponseEntity.ok()
            .body(new ResponseQuery(results));
    }

    /**
     * {@code PUT  /photoRDF} : Creates or Updates a photoRDF
     *
     * @param photoRDF the photoRDF to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoRDF,
     * or with status {@code 400 (Bad Request)} if the photoRDF is not valid,
     * or with status {@code 500 (Internal Server Error)} if the photoRDF couldn't be updated.
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/photoRDF")
    public ResponseEntity<PhotoRDF> updatePhotoRDF(
            @Valid @RequestBody PhotoRDF photoRDF)
            throws UnsupportedEncodingException {
        log.debug("REST request to update PhotoRDF : {}", photoRDF);

        RDFStore rdfStore = new RDFStore();

        Resource photoResource = rdfStore.createPhoto(photoRDF.getPhotoId(),
                photoRDF.getAlbumId(), photoRDF.getOwnerId());

        // Delete photos before update, otherwise it appends
        rdfStore.deleteClassUri(photoResource.getURI());

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

        log.debug("BELOW: MODEL BEFORE IT SAVED\n—————————————");
        model.write(System.out, "turtle");

        // print the graph on the standard output
        log.debug("BELOW: PRINT RESOURCE BEFORE IT SAVED"
                + "\n—————————————");
        photoResource.getModel().write(System.out, "turtle");

        rdfStore.saveModel(model);
        log.debug("BELOW: PRINT MODEL SAVED\n—————————————");
        rdfStore.readPhoto(photoRDF.getPhotoId(), true)
            .write(System.out, "turtle");

        return ResponseEntity.ok().body(photoRDF);
    }

    /**
     * {@code GET  /photoRDF/:id} : get the "id" photoRDF.
     *
     * @param id the id of the photoRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the photoRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/photoRDF/{id}")
    public ResponseEntity<PhotoRDF> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get photoRDF : {}", id);
        RDFStore rdfStore = new RDFStore();
        if (!rdfStore.isUriIsSubject(Namespaces.getPhotoUri(id))) {
            log.error("PhotoRDF with uri '"
                    + Namespaces.getPhotoUri(id)
                    + "' doesn't exist in Fuseki Database"
                    + " (at least not a RDF subject).");
            return ResponseEntity.notFound().build();
        }
        Model model = rdfStore.readPhoto(id, true);
        long albumId = Integer.parseInt(
                model.listObjectsOfProperty(SempicOnto.albumId)
                .toList().get(0).toString().substring(0, 1)
                );
        long ownerId = Integer.parseInt(
                model.listObjectsOfProperty(SempicOnto.ownerId)
                .toList().get(0).toString().substring(0, 1)
                );
        List<Resource> depictObjects =
            model.listObjectsOfProperty(SempicOnto.depicts)
            .toList().parallelStream().map(RDFNode::asResource)
            .collect(Collectors.toList());
        PhotoDepictionRDF[] photoDepictionRDF =
            depictObjects.parallelStream().map(
                r -> {
                    String[] literalResources =
                        model.listObjectsOfProperty(r, RDFS.label).toList()
                        .parallelStream().map(RDFNode::toString)
                        .toArray(String[]::new);
                    // TODO it seams that properties are order from general to
                    // specific
                    // Not know if it's always that.
                    List<RDFNode> properties =
                        model.listObjectsOfProperty(r, RDF.type).toList();
                    return new PhotoDepictionRDF(
                        properties.get(properties.size() - 1).toString(),
                        literalResources
                    );
                }
            ).toArray(PhotoDepictionRDF[]::new);
        PhotoRDF photoRDF = new PhotoRDF(
                id,
                albumId,
                ownerId,
                photoDepictionRDF
                );
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        rdfStore.readPhoto(photoRDF.getPhotoId(), true)
            .write(System.out, "turtle");
        return ResponseEntity.ok().body(photoRDF);
    }

    /**
     * {@code DELETE  /photoRDF/:id} : delete the "id" photoRDF.
     *
     * @param id the id of the photoRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/photoRDF/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        log.debug("REST request to delete photoRDF : {}", id);

        String photoUri = Namespaces.getPhotoUri(id);

        RDFStore rdfStore = new RDFStore();

        if (!rdfStore.isUriIsSubject(photoUri)) {
            log.error("Photo with uri '" + photoUri + "' doesn't exist"
                    + " in Fuseki database"
                    + " (at least not a RDF subject)"
                    + ", can't be deleted.");
            return ResponseEntity.notFound().build();
        }
        log.debug("Photo with uri '" + photoUri + "' exists"
                + ", this RDF subject will be deleted.");

        rdfStore.deleteClassUri(photoUri);
        if (rdfStore.isUriIsSubject(photoUri)) {
            throw new FusekiJenaQueryException("Photo with uri " + photoUri
                    + " not deleted.");
        }
        log.debug("Photo with uri '" + photoUri + "' doesn't exist"
                + ", therefore it was deleted.");
        return ResponseEntity.noContent().build();
    }

}
