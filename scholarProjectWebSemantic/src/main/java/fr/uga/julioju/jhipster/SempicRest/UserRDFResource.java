package fr.uga.julioju.jhipster.SempicRest;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

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

import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;

/**
 * REST controller for managing UserRDFREsource.
 */
@RestController
@RequestMapping("/api")
public class UserRDFResource  {

    private final Logger log = LoggerFactory.getLogger(UserRDFResource.class);

    /**
     * {@code PUT  /instantiateInitialUsers} : Creates admin and normal user
     *
     */
    @GetMapping("/instantiateInitialUsers")
    public ResponseEntity<UserRDF[]> updateUserRDF()
            throws UnsupportedEncodingException {
        log.debug("REST request to instantiate initial users");

        Model model = ModelFactory.createDefaultModel();
        UserRDF normalUserRDF = new UserRDF(
                "user",
                "user",
                UserGroup.NORMAL_USER_GROUP
            );;
        Resource normalUserResource = CreateResource
            .createUserLogged(model, normalUserRDF);
        UserRDF adminUserRDF = new UserRDF(
                "admin",
                "admin",
                UserGroup.ADMIN_GROUP
            );;
        Resource adminUserResource = CreateResource
            .createUserLogged(model, adminUserRDF);



        if (!RDFStore.isUriIsSubject(normalUserResource.getURI())
                || !RDFStore.isUriIsSubject(adminUserResource.getURI())) {
            log.debug("User " + normalUserResource.getURI()
                    + " not already created. They will be created.");
            log.debug("User " + adminUserResource.getURI()
                    + " not already created. They will be created");
            RDFConn.saveModel(model);
        } else {
            log.warn("User " + normalUserResource.getURI() + " or "
                    + adminUserResource.getURI() + " already created");
            // TODO
            throw new UnsupportedOperationException("Can\'t update user "
                    + "with id 3 and 4.");
        }

        return ResponseEntity.ok()
            .body(new UserRDF[] { normalUserRDF, adminUserRDF});
    }

    /**
     * {@code GET  /userRDF/:id} : get the "id" userRDF.
     *
     * @param id the id of the userRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/userRDF/{id}")
    public ResponseEntity<UserRDF> getUser(@PathVariable String login) {
        log.debug("REST request to get userRDF : {}", login);
        Optional<UserRDF> userRDF = ReadUser.getUserByLogin(login);
        if (userRDF.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
        return ResponseEntity.ok().body(userRDF.get());
        }
    }

    /**
     * {@code DELETE  /userRDF/:id} : delete the "id" userRDF.
     *
     * @param id the id of the userRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/userRDF/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete userRDF : {}", login);

        String userUri = Namespaces.getUserUri(login);

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
