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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Delete;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.ReadPhoto;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;

/**
 * REST controller for managing PhotoRDFResource.
 */
@RestController
@RequestMapping("/api")
public class PhotoRDFResource {

    private final Logger log = LoggerFactory.getLogger(PhotoRDFResource.class);

    /**
     * {@code PUT  /photoRDF} : Creates or Updates a photoRDF
     *
     * @param photoRDF the photoRDF to update.
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with
     * body the updated entity,
     * or with status {@code 201 (created)} and with body the created {@link PhotoRDF},
     * Errors:
     * status {@code 400 (Bad Request)} if the {@link PhotoRDF} is not valid,
     * status {@code 500 (Internal Server Error)} if the {@link PhotoRDF} couldn't be updated.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @PutMapping("/photoRDF")
    public ResponseEntity<PhotoRDF> createOrUpdate(
            @Valid @RequestBody PhotoRDF photoRDF) {
        log.debug("REST request to update PhotoRDF : {}", photoRDF);

        AlbumRDF albumRDF = ReadAlbum.readAlbum(photoRDF.getAlbumId());
        ReadAlbum.testUserLoggedPermissions(albumRDF, false);

        Model model = ModelFactory.createDefaultModel();

        Resource photoResource = CreateResource.create(
                model,
                photoRDF
                );

        return CreateResource.createOrUpdate(
                photoResource,
                photoRDF,
                model
                // (Void) -> {
                //     return ReadPhoto.read(photoRDF.getId(), true);
                // }
        );
    }

    /**
     * {@code GET  /photoRDF/:id} : get the "id" photoRDF.
     *
     * @param id the id of the photoRDF
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/photoRDF/{id}")
    public ResponseEntity<PhotoRDF> getPhoto(@PathVariable Long id) {
        log.debug("REST request to get photoRDF : {}", id);

        PhotoRDF photoRDF = ReadPhoto.getPhotoById(id);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(photoRDF.getAlbumId());
        ReadAlbum.testUserLoggedPermissions(albumRDF, true);

        return ResponseEntity.ok().body(photoRDF);
    }

    /**
     * {@code DELETE  /photoRDF/:id} : delete the "id" photoRDF.
     * Note: photos are only subjects, never object,
     * needs only `deleteSubjectUri`
     *
     * @param id the id of the photoRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     * Errors:
     * status {@code 500 (Internal Server Error)} if the {@link PhotoRDF} couldn't be deleted.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @DeleteMapping("/photoRDF/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        log.debug("REST request to delete photoRDF : {}", id);
        String uri = Namespaces.getPhotoUri(id);

        PhotoRDF photoRDF = ReadPhoto.getPhotoById(id);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(photoRDF.getAlbumId());
        ReadAlbum.testUserLoggedPermissions(albumRDF, false);

        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        Delete.deleteSubjectUri(node_URI);
        return ResponseEntity.noContent().build();
    }

}
