package fr.uga.julioju.jhipster.SempicRest;

import javax.validation.Valid;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;

/**
 * REST controller for managing AlbumRDFResource.
 */
@RestController
@RequestMapping("/api")
public class AlbumRDFResource  {

    private final Logger log = LoggerFactory.getLogger(AlbumRDFResource.class);

    /** Test if user logged has permissions to manage album */
    private void testUserLoggedPermissions(AlbumRDF album) {
        ReadUser.testUserLoggedPermissions(
                "He is not the owner of the album with the id '"
                + album.getId()
                + "'"
                , album.getOwnerLogin()
        );
    }

    /**
     * {@code PUT  /albumRDF} : Creates or Updates a albumRDF
     *
     * @param albumRDF the albumRDF to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumRDF,
     * or with status {@code 400 (Bad Request)} if the albumRDF is not valid,
     * or with status {@code 500 (Internal Server Error)} if the albumRDF couldn't be updated.
     */
    @PutMapping("/albumRDF")
    public ResponseEntity<AlbumRDF> createOrUpdate (
            @Valid @RequestBody AlbumRDF albumRDF) {
        log.debug("REST request to update AlbumRDF : {}", albumRDF);

        if (!RDFStore.isUriIsSubject(
                (Node_URI) NodeFactory.createURI(
                    Namespaces.getUserUri(albumRDF.getOwnerLogin())
                    )
        )) {
            throw new FusekiSubjectNotFoundException(
                "The user '" + albumRDF.getOwnerLogin() + "' doesn't exist, "
                + "therefore it can't be the owner of the album you try "
                + "to create.");
        }
        this.testUserLoggedPermissions(albumRDF);
        Model model = ModelFactory.createDefaultModel();
        CreateResource.create(model, albumRDF);
        log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
        model.write(System.out, "turtle");
        RDFConn.saveModel(model);
        return ResponseEntity.ok().body(albumRDF);
    }

    /**
     * {@code GET  /albumRDF/:id} : get the "id" albumRDF.
     *
     * @param id the id of the albumRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the albumRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/albumRDF/{id}")
    public ResponseEntity<AlbumRDF> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get albumRDF : {}", id);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(id);
        this.testUserLoggedPermissions(albumRDF);
        return ResponseEntity.ok().body(albumRDF);
    }

    /**
     * {@code DELETE  /albumRDF/:id} : delete the "id" albumRDF.
     *
     * @param id the id of the albumRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/albumRDF/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        log.debug("REST request to delete albumRDF : {}", id);
        String uri = Namespaces.getAlbumUri(id);
        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(id);
        this.testUserLoggedPermissions(albumRDF);
        RDFStore.deleteClassUriWithTests(node_URI);
        return ResponseEntity.noContent().build();
    }

}
