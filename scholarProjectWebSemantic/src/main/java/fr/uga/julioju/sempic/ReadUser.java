package fr.uga.julioju.sempic;

import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.uga.julioju.jhipster.security.SecurityUtils;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.Exceptions.SpringSecurityTokenException;
import fr.uga.julioju.sempic.Exceptions.TokenOutOfDateException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadUser extends AbstractRead {

    private static final Logger log =
        LoggerFactory.getLogger(ReadUser.class);

    private static Model read(String login) {
        String uriLogin = Namespaces.getUserUri(login);
        Node_URI node_URILogin = (Node_URI) NodeFactory.createURI(uriLogin);

        // CONSTRUCT and WHERE clauses preparation
        Triple tripleUser = Triple.create(
                node_URILogin,
                RDF.type.asNode(),
                Var.alloc("adminGroup")
                );

        // Prepare WHERE clause
        Triple tripleSubClassOf = Triple.create(
                Var.alloc("adminGroup"),
                RDFS.subClassOf.asNode(),
                SempicOnto.User.asNode());
        BasicPattern basicPatternWhere = new BasicPattern();
        basicPatternWhere.add(tripleUser);
        basicPatternWhere.add(tripleSubClassOf);
        Op op = new OpBGP(basicPatternWhere);

        // Prepare CONSTRUCT clause
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleUser);
        return AbstractRead.read(node_URILogin, basicPattern, op);
    }

    /** Test if user logged has permissions to manage album */
    public static void testUserLoggedPermissions(String message,
            String loginResourceOwner,
            Optional<AlbumRDF> albumRDFOptional) {
        log.debug("\n\nTest if the user logged has permissions");
        UserRDF userLogged;
        try {
            userLogged = ReadUser.getUserLogged();
        } catch (FusekiSubjectNotFoundException e) {
            throw new TokenOutOfDateException(e);
        }
        if (albumRDFOptional.isPresent()) {
            AlbumRDF albumRDF = albumRDFOptional.get();
            String[] sharedWithLoginArray = albumRDF.getSharedWith();
            if (sharedWithLoginArray != null) {
                for (String sharedWithLogin : sharedWithLoginArray) {
                    if (userLogged.getLogin().equals(sharedWithLogin)) {
                        log.info("The album with id '"
                                + albumRDF.getId()
                                + " 'is shared by the current user logged '"
                                + userLogged.getLogin()
                                );
                        return;
                    }
                }
            }
        }
        if (! ReadUser.isUserLoggedAdmin(userLogged)
                && ! userLogged.getLogin().equals(loginResourceOwner)
        ) {
            throw new AccessDeniedException(
                    "The current user is '"
                    + userLogged.getLogin()
                    + "'. " + message
                    + " Furthermore he is not an administrator."
            );
        }
    }

    public static UserRDF getUserByLogin(String login) {
        log.debug("Get userRDF : {}", login);
        Model model = ReadUser.read(login);
        UserGroup userGroup = UserGroup.NORMAL_USER_GROUP;
        if (
            model.listObjectsOfProperty(RDF.type)
            .toList().contains(SempicOnto.UserGroupAdmin)
        ) {
            userGroup = UserGroup.ADMIN_GROUP;
        }
        UserRDF userRDF = new UserRDF(login, "NOT RETRIEVED (saved as a hash)",
                userGroup);
        return userRDF;
    }

    public static UserRDF getUserLogged() {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if (login.isEmpty()) {
            throw new SpringSecurityTokenException();
        }
        UserRDF userRDF;
        try {
            userRDF = ReadUser.getUserByLogin(login.get());
        } catch (FusekiSubjectNotFoundException e) {
            throw new TokenOutOfDateException(e);
        }
        if (! SecurityUtils.isCurrentUserInRole(
                    userRDF.getUserGroup().toString())
        ) {
            String springSecurityRole = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::toString)
                .collect(Collectors.joining(" ,"));
            throw new TokenOutOfDateException(springSecurityRole);
        }
        return userRDF;
    }

    public static boolean isUserLoggedAdmin(UserRDF userRDF) {
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            return true;
        }
        return false;
    }


}

