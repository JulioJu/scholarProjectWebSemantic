package fr.uga.julioju.jhipster.SempicRest;

import java.util.List;
import java.util.function.Function;

import org.apache.jena.shared.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.ReadAlbum;
import fr.uga.julioju.sempic.ReadUser;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;

/**
 * REST controller for managing AlbumRDFResourceGet.
 */
@RestController
@RequestMapping("/api")

public class AlbumRDFResourceGet  {

    private final Logger log = LoggerFactory.getLogger(AlbumRDFResourceGet.class);

    /**
     * {@code GET  /albumRDF/:id} : get the "id" albumRDF.
     *
     * @param id the id of the albumRDF to retrieve.
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * or with status {@code 201 (created)} and with body the {@link AlbumRDF},
     * Errors:
     * status {@code 500 (Internal Server Error)} if the {@link AlbumRDF} couldn't be updated.
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumRDF/{id}")
    public ResponseEntity<AlbumRDF> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get albumRDF : {}", id);
        AlbumRDF albumRDF = ReadAlbum.readAlbum(id);
        ReadAlbum.testUserLoggedPermissions(albumRDF, true);
        return ResponseEntity.ok().body(albumRDF);
    }

    /**
     * {@code GET  /allAlbums} : if the user logged is admin get all albums
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/allAlbums")
    public ResponseEntity<List<AlbumRDF>> getAllAlbums() {
        if (! ReadUser.isUserLoggedAdmin(ReadUser.getUserLogged())) {
            throw new AccessDeniedException(
                    "FORBIDDEN: current user is not an admin");
        }
        try {
            return ResponseEntity.ok(ReadAlbum.readAllAlbums());
        } catch (FusekiSubjectNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    private static ResponseEntity<List<AlbumRDF>> getSeveralAlbumsUserLogged (
            Function<String, List<AlbumRDF>> readAlb
    ){
        try {
            return ResponseEntity.ok(
                    readAlb.apply( ReadUser.getUserLogged().getLogin())
            );
        } catch (FusekiSubjectNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    private static ResponseEntity<List<AlbumRDF>> getSeveralAlbums(String login,
            Function<String, List<AlbumRDF>> readAlb) {
        login = login.toLowerCase();

        ReadUser.testIfUserExists(login);

        if (! ReadUser.isUserLoggedAdmin(ReadUser.getUserLogged())) {
            throw new AccessDeniedException(
                    "FORBIDDEN: current user is not an admin");
        }
        try {
            return ResponseEntity.ok(readAlb.apply(login));
        } catch (FusekiSubjectNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * {@code GET  /albumsOfUserLogged} : get all albums owned by the current user logged
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumsOfUserLogged")
    public ResponseEntity<List<AlbumRDF>> getAlbumsOfUserLogged() {
        return AlbumRDFResourceGet
            .getSeveralAlbumsUserLogged(ReadAlbum::readAlbumsOfAnUser);
    }

    /**
     * {@code GET  /getUserSAlbums/:login} : if the user
     *   logged is admin get all albums owned by the
     *   user with login :login.
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/getUserSAlbums/{login}")
    public ResponseEntity<List<AlbumRDF>> getUserSAlbums(
            @PathVariable String login) {
        return AlbumRDFResourceGet.getSeveralAlbums(login,
                ReadAlbum::readAlbumsOfAnUser
                );
    }

    /**
     * {@code GET  /albumsSharedWithUserLogged} : get all albums
     *      shared with current user logged
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumsSharedWithUserLogged")
    public ResponseEntity<List<AlbumRDF>> getAlbumSharedWithUserLogged() {
        return AlbumRDFResourceGet
            .getSeveralAlbumsUserLogged(ReadAlbum::readAlbumsSharedWithAnUser);
    }

    /**
     * {@code GET  /albumsSharedWith/:login} : if the user
     *   logged is admin get all albums shared with the user with login :login
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumsSharedWith/{login}")
    public ResponseEntity<List<AlbumRDF>> getAlbumsSharedWith(
            @PathVariable String login) {
        return AlbumRDFResourceGet.getSeveralAlbums(login,
                    ReadAlbum::readAlbumsSharedWithAnUser
                );
    }

    /**
     * {@code GET  /albumsSharedWithUserLogged} : get all albums
     *  owned by and shared with current user logged
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumsOwnedByAndSharedWithUserLogged")
    public ResponseEntity<List<AlbumRDF>> getAlbumsOwnedAndSharedWithUserLogged() {
        return AlbumRDFResourceGet
            .getSeveralAlbumsUserLogged(ReadAlbum::readAlbumsOwnedByAndSharedWith);
    }

    /**
     * {@code GET  /albumsOwnedAndSharedWith/:login} : if the user
     *   logged is admin get all albums owned by and shared with the
     *   user with login :login.
     *
     * @return the {@link ResponseEntity}
     * with status {@code 200 (OK)} and with the entity in the body,
     * with status {@code 204 (NO_CONTENT)} if the user has no album
     * Errors:
     * status {@code 409 (Conflict)} if the authentification token is outdated with the state of the database
     * status {@code 403 (Forbidden)} if the user has no the permission to read
     * (not owner or not administrator)
     * status {@code 404 (Not found)} if a resource used in the request
     * in not found in the database.
     */
    @GetMapping("/albumsOwnedByAndSharedWith/{login}")
    public ResponseEntity<List<AlbumRDF>> getAlbumsOwnedAndSharedWith(
            @PathVariable String login) {
        return AlbumRDFResourceGet.getSeveralAlbums(login,
                    ReadAlbum::readAlbumsOwnedByAndSharedWith
                );
    }

}
