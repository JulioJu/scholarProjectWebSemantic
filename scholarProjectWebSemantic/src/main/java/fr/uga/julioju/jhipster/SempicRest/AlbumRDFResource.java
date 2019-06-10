package fr.uga.julioju.jhipster.SempicRest;

import javax.validation.Valid;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.Ask;
import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Delete;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;

/**
 * REST controller for managing AlbumRDFResource.
 */
@RestController
@RequestMapping("/api")
public class AlbumRDFResource  {

    private final Logger log = LoggerFactory.getLogger(AlbumRDFResource.class);

    /**
     * {@code PUT  /albumRDF} : Creates or Updates a albumRDF
     *
     * @param albumRDF the albumRDF
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with
     * body the updated entity,
     * or with status {@code 201 (created)} and with body the created {@link AlbumRDF},
     * Errors:
     * status {@code 400 (Bad Request)} if the {@link AlbumRDF} is not valid,
     * status {@code 500 (Internal Server Error)} if the albumRDF couldn't be updated.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @PutMapping("/albumRDF")
    public ResponseEntity<AlbumRDF> createOrUpdate (
            @Valid @RequestBody AlbumRDF albumRDF) {
        log.debug("REST request to update AlbumRDF : {}", albumRDF);

        if (!Ask.isUriIsSubject(
                (Node_URI) NodeFactory.createURI(
                    Namespaces.getUserUri(albumRDF.getOwnerLogin())
                    )
        )) {
            throw new FusekiSubjectNotFoundException(
                "The user '" + albumRDF.getOwnerLogin() + "' doesn't exist, "
                + "therefore it can't be the owner of the album you try "
                + "to create.");
        }

        ReadAlbum.testUserLoggedPermissions(albumRDF, false);
        Model model = ModelFactory.createDefaultModel();
        Resource resource = CreateResource.create(model, albumRDF);
        boolean isUpdate = false;
        if (Ask.isUriIsSubject((Node_URI) resource.asNode())) {
            isUpdate = true;
        }

        log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
        model.write(System.out, "turtle");
        if (isUpdate) {
            Delete.deleteSubjectUri((Node_URI) NodeFactory.createURI(
                        Namespaces.getAlbumUri(albumRDF.getId())));
        }
        RDFConn.saveModel(model);
        if (isUpdate) {
            return ResponseEntity.ok().body(albumRDF);
        } else {
            return ResponseEntity.status(201).body(albumRDF);
        }
    }

    /**
     * {@code DELETE  /albumRDF/:id} : delete the "id" albumRDF.
     *
     * @param id the id of the albumRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     * Errors:
     * status {@code 500 (Internal Server Error)} if the {@link AlbumRDF} couldn't be deleted.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @DeleteMapping("/albumRDF/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        log.debug("REST request to delete albumRDF : {}", id);
        String uri = Namespaces.getAlbumUri(id);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(id);
        ReadAlbum.testUserLoggedPermissions(albumRDF, false);
        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        Delete.deleteAlbumsAndItsPhotos(node_URI);
        return ResponseEntity.noContent().build();
    }

}
