package fr.uga.julioju.jhipster.SempicRest;

import java.util.List;
import java.util.Optional;

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

import fr.uga.julioju.sempic.Ask;
import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Delete;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;

/**
 * REST controller for managing UserRDFREsource.
 * VERY IMPORTANT NOTE: ALL LOGIN ARE CONVERTED TO LOWER CASE FOR
 * SECURITY REASONS
 */
@RestController
@RequestMapping("/api")
public class UserRDFResource  {

    private final Logger log = LoggerFactory.getLogger(UserRDFResource.class);

    private final PasswordEncoder passwordEncoder;

    public UserRDFResource(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /** Test if user logged has permissions to manage user */
    private void testUserLoggedPermissions(String login) {
        ReadUser.testUserLoggedPermissions(
                "He is not the user you try to change ('"
                + login
                + "')."
                , login
                , Optional.empty()
        );
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
                UserGroup.NORMAL_USER_GROUP
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

        if (!Ask.isUriIsSubject((Node_URI) normalUserResource.asNode())
                && !Ask.isUriIsSubject((Node_URI) adminUserResource.asNode())) {
            log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
            model.write(System.out, "turtle");
            RDFConn.saveModel(model);
        } else {
            // TODO
            throw new UnsupportedOperationException("Users 'admin' and 'user'"
                    + " can't be updated with this API.");
        }

        return ResponseEntity.ok()
            .body(new UserRDF[] { normalUserRDF, adminUserRDF});
    }

    /**
     * @param userRDF the userRDF
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with
     * body the updated entity,
     * or with status {@code 201 (created)} and with body the created {@link UserRDF},
     * Errors:
     * status {@code 400 (Bad Request)} if the {@link UserRDF} is not valid,
     * status {@code 500 (Internal Server Error)} if the {@link UserRDF} couldn't be updated.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @PutMapping("/register")
    public ResponseEntity<UserRDF> createOrUpdate(
            @Valid @RequestBody UserRDF userRDF) {
        UserRDF userRDFToSave = new UserRDF(
                userRDF.getLogin().toLowerCase(),
                passwordEncoder.encode(userRDF.getPassword()),
                userRDF.getUserGroup()
        );
        this.testUserLoggedPermissions(userRDFToSave.getLogin());
        Model model = ModelFactory.createDefaultModel();
        Resource UserRDFResource = CreateResource.create(model, userRDFToSave);
        if (!Ask.isUriIsSubject((Node_URI) UserRDFResource.asNode())) {
            if (!ReadUser.isUserLoggedAdmin(ReadUser.getUserLogged())) {
                throw new AccessDeniedException("Only an administrator"
                        + " could register a new user.");
            }
            log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
            model.write(System.out, "turtle");
            RDFConn.saveModel(model);
            return ResponseEntity.status(20).body(userRDFToSave);
        } else {
            log.info("User '" + UserRDFResource.getURI() + "' already created");
            // NOTE: ReadUser.getUserLogged() is triggered twice, in functions
            // this.testUserLoggedPermissions() and in function below
            UserRDF userLogged = ReadUser.getUserLogged();
            if (!ReadUser.isUserLoggedAdmin(userLogged)) {
                if (! userRDFToSave.getUserGroup().equals(userLogged.getUserGroup())) {
                    throw new AccessDeniedException("Only a "
                            + UserGroup.ADMIN_GROUP.toString()
                            + " could change the user group."
                            + " Current UserGroup of the user '"
                            + userRDFToSave.getLogin()
                            + "'" + " is '" + userLogged.getUserGroup()
                            + "'. You tried to change it to '"
                            + userRDFToSave.getUserGroup());
                }
            }
            Delete.deleteSubjectUri((Node_URI) NodeFactory.createURI(
                        Namespaces.getUserUri(userRDFToSave.getLogin())));
            RDFConn.saveModel(model);
            return ResponseEntity.ok().body(userRDFToSave);
        }
    }

    /**
     * {@code GET  /userRDF/:login} : get the "login" userRDF.
     *
     * @param login the login of the userRDF to retrieve.
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/userRDF/{login}")
    public ResponseEntity<UserRDF> getUser(@PathVariable String login) {
        login = login.toLowerCase();
        log.debug("REST request to get userRDF : {}", login);
        return ResponseEntity.ok().body(ReadUser.getUserByLogin(login));
    }

    /**
     * {@code DELETE  /userRDF/:login} : delete the "login" userRDF.
     *
     * @param login the login of the userRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     * Errors:
     * status {@code 500 (Internal Server Error)} if the {@link UserRDF} couldn't be deleted.
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @DeleteMapping("/userRDF/{login}")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        login = login.toLowerCase();
        log.debug("REST request to delete userRDF : {}", login);
        this.testUserLoggedPermissions(login);
        String uri = Namespaces.getUserUri(login);
        try {
            List<AlbumRDF> albumSharedWithCurrentUser =
                ReadAlbum.readAlbumsSharedWithAnUser(login);
            AlbumRDFResource albumRDFResource = new AlbumRDFResource();
            albumSharedWithCurrentUser.forEach(
                albumRDF -> {
                    String [] albumSharedWithOrigin = albumRDF.getSharedWith();
                    String [] albumRDFShareWithNew =
                        new String[albumSharedWithOrigin.length - 1];
                    int indexArraySource = 0;
                    int indexArrayDest = 0;
                    while (indexArrayDest < albumRDFShareWithNew.length) {
                        if (! albumSharedWithOrigin[indexArraySource]
                                .equals(uri)
                        ) {
                            albumRDFShareWithNew[indexArrayDest] =
                                albumSharedWithOrigin[indexArraySource];
                            indexArrayDest ++;
                        }
                        indexArraySource++;
                    }

                    albumRDF = new AlbumRDF(
                            albumRDF.getId(),
                            albumRDF.getTitle(),
                            albumRDF.getOwnerLogin(),
                            albumRDFShareWithNew
                            );
                    albumRDFResource.createOrUpdate(albumRDF);
                });
        } catch (FusekiSubjectNotFoundException e ) {
            log.info("So cool, this user has no albums shared with him");
        }
        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        Delete.deleteUserAndItsAlbumsAndItsPhotos(node_URI);
        return ResponseEntity.noContent().build();
    }

}
