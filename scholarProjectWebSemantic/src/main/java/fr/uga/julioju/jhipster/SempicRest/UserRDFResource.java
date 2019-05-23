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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;

/**
 * REST controller for managing UserRDFREsource.
 */
@RestController
@RequestMapping("/api")
public class UserRDFResource  {

    private final Logger log = LoggerFactory.getLogger(UserRDFResource.class);

    private final PasswordEncoder passwordEncoder;

    public UserRDFResource(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@code GET  /instantiateInitialUsers} : Creates admin and normal user
     *
     */
    @GetMapping("/createInitialUser")
    public ResponseEntity<UserRDF[]> createInitialiUser() {
        log.debug("REST request to instantiate initial users");

        Model model = ModelFactory.createDefaultModel();
        UserRDF normalUserRDF = new UserRDF(
                "user",
                passwordEncoder.encode("user"),
                UserGroup.ADMIN_GROUP
            );;
        Resource normalUserResource = CreateResource
            .create(model, normalUserRDF);
        UserRDF adminUserRDF = new UserRDF(
                "admin",
                passwordEncoder.encode("admin"),
                UserGroup.ADMIN_GROUP
            );;
        Resource adminUserResource = CreateResource
            .create(model, adminUserRDF);

        if (!RDFStore.isUriIsSubject((Node_URI) normalUserResource.asNode())
                && !RDFStore.isUriIsSubject((Node_URI) adminUserResource.asNode())) {
            log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
            model.write(System.out, "turtle");
            RDFConn.saveModel(model);
        } else {
            log.warn("User '" + normalUserResource.getURI() + "' or '"
                    + adminUserResource.getURI() + "' already created");
            // TODO
            throw new UnsupportedOperationException("Can\'t update user ");
        }

        return ResponseEntity.ok()
            .body(new UserRDF[] { normalUserRDF, adminUserRDF});
    }

    @PutMapping("/register")
    public ResponseEntity<UserRDF> createOrUpdate(
            @Valid @RequestBody UserRDF userRDF) {
            ReadUser.getUserLogged();
        if (!ReadUser.isUserLoggedAdmin(ReadUser.getUserLogged())) {
            throw new AccessDeniedException("Only an administrator"
                    + " could register a new user.");
        }

        UserRDF userRDFToSave = new UserRDF(
                userRDF.getLogin(),
                passwordEncoder.encode(userRDF.getPassword()),
                userRDF.getUserGroup()
        );
        Model model = ModelFactory.createDefaultModel();
        Resource UserRDFResource = CreateResource.create(model, userRDFToSave);
        if (!RDFStore.isUriIsSubject((Node_URI) UserRDFResource.asNode())) {
            log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
            model.write(System.out, "turtle");
            RDFConn.saveModel(model);
        } else {
            log.warn("User '" + UserRDFResource.getURI() + "' already created");
            // TODO
            throw new UnsupportedOperationException("Can\'t update user ");
        }
        return ResponseEntity.ok()
            .body(userRDFToSave);
    }

    /**
     * {@code GET  /userRDF/:login} : get the "login" userRDF.
     *
     * @param login the login of the userRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/userRDF/{login}")
    public ResponseEntity<UserRDF> getUser(@PathVariable String login) {
        log.debug("REST request to get userRDF : {}", login);
        return ResponseEntity.ok().body(ReadUser.getUserByLogin(login));
    }

    /**
     * {@code DELETE  /userRDF/:login} : delete the "login" userRDF.
     *
     * @param login the login of the userRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/userRDF/{login}")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete userRDF : {}", login);
        String uri = Namespaces.getUserUri(login);
        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        RDFStore.deleteClassUriWithTests(node_URI);
        return ResponseEntity.noContent().build();
    }

}
