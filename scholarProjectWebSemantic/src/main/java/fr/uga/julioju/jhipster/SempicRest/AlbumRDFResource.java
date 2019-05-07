package fr.uga.julioju.jhipster.SempicRest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.jhipster.domain.Authority;
import fr.uga.julioju.jhipster.domain.User;
import fr.uga.julioju.jhipster.security.AuthoritiesConstants;
import fr.uga.julioju.jhipster.service.UserService;
import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.Exceptions.FusekiUnauthorized;
import fr.uga.julioju.sempic.entities.AlbumRDF;

/**
 * REST controller for managing AlbumRDFResource.
 */
@RestController
@RequestMapping("/api")
public class AlbumRDFResource  {

    private final Logger log = LoggerFactory.getLogger(AlbumRDFResource.class);

    private UserService userService;

    public AlbumRDFResource(UserService userService) {
        this.userService = userService;
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

        User userLogged = this.userService.getUserWithAuthorities().get();
        Authority adminRole = new Authority();
        adminRole.setName(AuthoritiesConstants.ADMIN);
        if (
                userLogged.getAuthorities().contains(adminRole)
                || albumRDF.getOwnerId() ==
                    CreateResource.getUserIdLogged(userService)
        ) {
            RDFConn.saveModel(model);
        } else {
            // TODO create error
            throw new FusekiUnauthorized(
                    "User is not " + AuthoritiesConstants.ADMIN + " and "
                    + albumRDF.getOwnerId() + " is not the id of the user"
                    + " currently logged (currently logged : "
                    + CreateResource.getUserIdLogged(userService) + ").");
        }

        return ResponseEntity.ok().body(albumRDF);
    }

}
