package fr.uga.julioju.sempic;

import java.util.Optional;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.jhipster.security.SecurityUtils;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.Exceptions.SpringSecurityTokenException;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadUser  {

    private static final Logger log =
        LoggerFactory.getLogger(ReadUser.class);

    /**
     * Query a Photo and retrieve all the direct properties
     * of the photo and if the property are depicts, albumId and
     * ownerId
     * If the object has RDFS.label and RDF.type, retrieve the triple
     *
     * @param id
     * @return
     */
    public static Model read(String login) {
        String uri = Namespaces.getUserUri(login);

        Triple tripleUri = Triple.create(
                    NodeFactory.createURI(uri),
                    Var.alloc("p"),
                    Var.alloc("o"));

        Triple tripleLabel = Triple.create(
                    Var.alloc("o"),
                    RDFS.label.asNode(),
                    Var.alloc("o2"));

        Triple tripleRDFType = Triple.create(
                    Var.alloc("o"),
                    RDF.type.asNode(),
                    Var.alloc("o3"));

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleUri);
        basicPattern.add(tripleLabel);
        basicPattern.add(tripleRDFType);

        BasicPattern basicPatternWhere = new BasicPattern();
        basicPatternWhere.add(tripleUri);

        BasicPattern basicPatterWhereOptional = new BasicPattern();
        basicPatterWhereOptional.add(tripleLabel);
        basicPatterWhereOptional.add(tripleRDFType);

        // Java API 2) Algebra form of the query
        // —————————————————————

        Op opLeft = new OpBGP(basicPatternWhere);
        Op opRight = new OpBGP(basicPatterWhereOptional);

        Op op = OpLeftJoin.createLeftJoin(opLeft, opRight, null);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryConstructType();

        queryAlgebraBuild.setConstructTemplate(new Template(basicPattern));

        log.debug("queryAlgebraBuild\n" + queryAlgebraBuild);

        // Execution
        // —————————

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);
        return m;
    }

    public static Optional<UserRDF> getUserByLogin(String login) {
        log.debug("REST request to get userRDF : {}", login);
        Model model = ReadUser.read(login);
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        model.write(System.out, "turtle");
        if (model.isEmpty()) {
            log.error("UserRDF with uri '"
                    + Namespaces.getUserUri(login)
                    + "' doesn't exist in Fuseki Database"
                    + " (at least not a RDF subject).");
            return Optional.empty();
        }
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
        return Optional.of(userRDF);
    }

    public static UserRDF getUserLogged() {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if (login.isEmpty()) {
            throw new SpringSecurityTokenException();
        }
        Optional<UserRDF> userRDF = SecurityUtils.getCurrentUserLogin().flatMap(
                ReadUser::getUserByLogin);
        if (userRDF.isEmpty()) {
            throw new FusekiSubjectNotFoundException("The user logged "
                    + "isn't found is the database (uri"
                    + Namespaces.getUserUri(login.get()));
        }
        return userRDF.get();
    }

    public static boolean isUserLoggedAdmin(UserRDF userRDF) {
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            return true;
        }
        return false;
    }


}

