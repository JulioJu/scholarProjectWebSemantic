package fr.uga.julioju.jhipster.SempicRest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.Exceptions.FusekiUnauthorized;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.UserRDF;

/**
 * REST controller for managing AlbumRDFResource.
 */
@RestController
@RequestMapping("/api")
public class AlbumRDFResource  {

    private final Logger log = LoggerFactory.getLogger(AlbumRDFResource.class);

    /** Test if user logged has permissions to manage album */
    private boolean isCurrentUserHasWritePermissions(AlbumRDF album) {
        UserRDF userLogged = ReadUser.getUserLogged();
        if (ReadUser.isUserLoggedAdmin(userLogged)) {
            return true;
        } else {
            // TODO create error
            throw new FusekiUnauthorized(
                    "The current user is '"
                    + userLogged.getLogin()
                    + "'. He is not the owner of the album with the id '"
                    + album.getId()
                    + "'. Furthermore he is not an administrator."
                );
        }
    }

    /**
     * {@code PUT  /albumRDF} : Creates or Updates a albumRDF
     *
     * @param albumRDF the albumRDF to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumRDF,
     * or with status {@code 400 (Bad Request)} if the albumRDF is not valid,
     * or with status {@code 500 (Internal Server Error)} if the albumRDF couldn't be updated.
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/albumRDF")
    public ResponseEntity<AlbumRDF> updateUserRDF(
            @Valid @RequestBody AlbumRDF albumRDF)
            throws UnsupportedEncodingException {
        log.debug("REST request to update AlbumRDF : {}", albumRDF);

        Model model = ModelFactory.createDefaultModel();

        if (this.isCurrentUserHasWritePermissions(albumRDF)) {
            RDFConn.saveModel(model);
        }
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
        Model model = ReadAlbum.read(id);
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        model.write(System.out, "turtle");
        if (model.isEmpty()) {
            log.error("AlbumRDF with uri '"
                    + Namespaces.getAlbumUri(id)
                    + "' doesn't exist in Fuseki Database"
                    + " (at least not a RDF subject).");
            return ResponseEntity.notFound().build();
        }
        AlbumRDF albumRDF = new AlbumRDF(id, 1);
        return ResponseEntity.ok().body(albumRDF);
    }


}
