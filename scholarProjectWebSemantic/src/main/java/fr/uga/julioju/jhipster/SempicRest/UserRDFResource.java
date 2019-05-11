package fr.uga.julioju.jhipster.SempicRest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.jhipster.service.UserService;
import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;
import fr.uga.julioju.sempic.entities.UserRDF;

/**
 * REST controller for managing UserRDFREsource.
 */
@RestController
@RequestMapping("/api")
public class UserRDFResource  {

    private final Logger log = LoggerFactory.getLogger(UserRDFResource.class);

    private UserService userService;

    public UserRDFResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code PUT  /userRDF} : Creates or Updates a userRDF
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userRDF,
     * or with status {@code 500 (Internal Server Error)} if the userRDF couldn't be updated.
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping("/createUserRDFLogged")
    public ResponseEntity<UserRDF> updateUserRDF()
            throws UnsupportedEncodingException {
        log.debug("REST request to update UserRDF logged");

        Model model = ModelFactory.createDefaultModel();

        Resource resource = CreateResource
            .createUserLogged(model, this.userService);


        if (!RDFStore.isUriIsSubject(resource.getURI())) {
            log.debug("User " + resource.getURI() + " not already created");
            RDFConn.saveModel(model);
        } else {
            log.warn("User " + resource.getURI() + " already created");
        }

        return ResponseEntity.ok()
            .body(
                new UserRDF(
                    this.userService.getUserWithAuthorities().get().getId()
                )
            );
    }

    /**
     * {@code GET  /userRDF/:id} : get the "id" userRDF.
     *
     * @param id the id of the userRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/userRDF/{id}")
    public ResponseEntity<UserRDF> getUser(@PathVariable Long id) {
        log.debug("REST request to get userRDF : {}", id);
        if (!RDFStore.isUriIsSubject(Namespaces.getUserUri(id))) {
            log.error("UserRDF with uri '"
                    + Namespaces.getUserUri(id)
                    + "' doesn't exist in Fuseki Database"
                    + " (at least not a RDF subject).");
            return ResponseEntity.notFound().build();
        }
        UserRDF userRDF = new UserRDF(id);
        return ResponseEntity.ok().body(userRDF);
    }

    /**
     * {@code DELETE  /userRDF/:id} : delete the "id" userRDF.
     *
     * @param id the id of the userRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/userRDF/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete userRDF : {}", id);

        String userUri = Namespaces.getUserUri(id);

        if (!RDFStore.isUriIsSubject(userUri)) {
            log.error("User with uri '" + userUri + "' doesn't exist"
                    + " in Fuseki database"
                    + " (at least not a RDF subject)"
                    + ", can't be deleted.");
            return ResponseEntity.notFound().build();
        }
        log.debug("User with uri '" + userUri + "' exists"
                + ", this RDF subject will be deleted.");

        RDFStore.deleteClassUri(userUri);
        if (RDFStore.isUriIsSubject(userUri)) {
            throw new FusekiUriNotAClass("User with uri " + userUri
                    + " not deleted.");
        }
        log.debug("User with uri '" + userUri + "' doesn't exist"
                + ", therefore it was deleted.");
        return ResponseEntity.noContent().build();
    }

}
