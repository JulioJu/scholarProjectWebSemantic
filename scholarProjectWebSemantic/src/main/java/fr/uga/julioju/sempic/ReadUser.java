package fr.uga.julioju.sempic;

import java.util.Optional;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.jhipster.security.SecurityUtils;
import fr.uga.julioju.sempic.Exceptions.SpringSecurityTokenException;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadUser extends AbstractRead {

    private static final Logger log =
        LoggerFactory.getLogger(ReadUser.class);

    private static Model read(String login) {
        String uri = Namespaces.getUserUri(login);
        return AbstractRead.read((Node_URI) NodeFactory.createURI(uri));
    }

    public static UserRDF getUserByLogin(String login) {
        log.debug("REST request to get userRDF : {}", login);
        Model model = ReadUser.read(login);
        String password = model.listObjectsOfProperty(SempicOnto.usersPassword)
            .toList().get(0).toString();
        UserGroup userGroup = UserGroup.NORMAL_USER_GROUP;
        if (
            model.listObjectsOfProperty(RDF.type)
            .toList().contains(SempicOnto.AdminGroup)
        ) {
            userGroup = UserGroup.ADMIN_GROUP;
        }
        UserRDF userRDF = new UserRDF(login, password, userGroup);
        return userRDF;
    }

    public static UserRDF getUserLogged() {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if (login.isEmpty()) {
            throw new SpringSecurityTokenException();
        }
        return ReadUser.getUserByLogin(login.get());
    }

    public static boolean isUserLoggedAdmin(UserRDF userRDF) {
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            return true;
        }
        return false;
    }


}

