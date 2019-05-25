package fr.uga.julioju.sempic;

import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;

/**
 *
 * @author https://github.com/JulioJu
 * TODO factorize
 */
public class RDFStore {

    private static final Logger log = LoggerFactory.getLogger(RDFStore.class);

    public static void deleteSubjectUri(Node_URI node_URI) {
            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + node_URI + "> ?p ?o }");
            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        node_URI,
                        Var.alloc("p"),
                        Var.alloc("o"))
            );
            UpdateRequest updateRequest =
                new UpdateRequest(new UpdateDeleteWhere(acc));
            log.debug("deleteClassUri\n" + updateRequest.toString());
            RDFConn.cnxUpdateRequest(updateRequest);
    }


    public static void deleteObjectUri() {
            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + node_URI + "> ?p ?o }");
            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        Var.alloc("s"),
                        Var.alloc("p"),
                        NodeFactory.createURI("http://fr.uga.julioju.sempic/ResourcesCreated/user/user")
                        )
            );
            UpdateRequest updateRequest =
                new UpdateRequest(new UpdateDeleteWhere(acc));
            log.debug("deleteClassUri\n" + updateRequest.toString());
            RDFConn.cnxUpdateRequest(updateRequest);
    }

    /**
     * Delete all the statements where the URI appears as subject or object
     * Delete with three levels:
     * When we delete a user, it its albums, and it deletes album's photo
     */
    public static void deleteClassUri(Node_URI node_URI) {

            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        node_URI,
                        Var.alloc("p1"),
                        Var.alloc("o1"))
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s2"),
                        Var.alloc("p2"),
                        Var.alloc("o2")
                        )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s2"),
                        Var.alloc("p3"),
                        node_URI
                        )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s3"),
                        Var.alloc("p4"),
                        Var.alloc("s2")
                        )
            );
            Update update = new UpdateDeleteWhere(acc);
            UpdateRequest updateRequest = new UpdateRequest(update);
            log.debug("deleteClassUri\n" + updateRequest.toString());

            RDFConn.cnxUpdateRequest(updateRequest);
    }

    public static void deleteClassUriWithTests(Node_URI node_URI) {
        String uri = node_URI.getURI();
        if (!RDFStore.isUriIsSubject(node_URI)) {
            throw new FusekiSubjectNotFoundException(node_URI);
        }
        log.debug("Uri '" + uri + "' exists"
                + ", this RDF subject will be deleted.");

        RDFStore.deleteClassUri(node_URI);

        if (RDFStore.isUriIsSubject(node_URI)) {
            throw new FusekiUriNotAClass("Uri '" + uri
                    + "' not deleted.");
        }
        log.debug("Now uri '" + uri + "' doesn't exist as subject"
                + ", therefore it was deleted (as subject).");
    }

    // Reference https://users.jena.apache.narkive.com/dMOMKIO8/sparql-to-check-if-a-specific-uri-exists
    public static boolean isUriIsClass(Node_URI node_URI) {

        // String s = "ASK WHERE {"
        //     + "{ <" + uri + "> ?p ?o . }"
        //     + " UNION "
        //     + "{?s ?p <" + uri + "> . }"
        //     + "}";

        Triple tripleSubject = Triple.create(
                    node_URI,
                    Var.alloc("p"),
                    Var.alloc("o"));
        BasicPattern basicPatternSubject = new BasicPattern();
        basicPatternSubject.add(tripleSubject);
        Op opSubject = new OpBGP(basicPatternSubject);

        Triple objectTriple = Triple.create(
                    Var.alloc("s"),
                    Var.alloc("p"),
                    node_URI
                    );
        BasicPattern basicPatternObject = new BasicPattern();
        basicPatternObject.add(objectTriple);
        Op opObject = new OpBGP(basicPatternObject);

        Op op = OpUnion.create(opSubject, opObject);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryAskType();

        log.debug("testIfUriIsClass\n" + queryAlgebraBuild);

        return RDFConn.cnxQueryAsk(queryAlgebraBuild);
    }

    public static void testIfUriIsClass(Node_URI node_URI) {
        if (!RDFStore.isUriIsClass(node_URI)) {
            throw new FusekiUriNotAClass("'" + node_URI.getURI()
                    + "' is not a RDF class");
        }
    }

    public static boolean isUriIsSubject(Node_URI node_URI) {
        Triple triple = Triple.create(
                    node_URI,
                    Var.alloc("p"),
                    Var.alloc("o"));
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(triple);
        Op op = new OpBGP(basicPattern);
        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryAskType();
        log.debug("isUriIsSubject\n" + queryAlgebraBuild);
        boolean result = RDFConn.cnxQueryAsk(queryAlgebraBuild);
        return result;
    }

    /**
     * Retieves all the resources that are subclasses of resource c. To be
     * selected classes must have the property rdfs:label instanciated
     *
     * @return
     */
    public static List<Resource> listSubClassesOf(Node_URI node_URI) {

        RDFStore.testIfUriIsClass(node_URI);

        // String s = "CONSTRUCT { "
        //     + "?s <" + RDFS.label + "> ?o "
        //     + "} WHERE {"
        //     + "?s <" + RDFS.subClassOf + "> <" + node_URI + "> ."
        //     + "?s <" + RDFS.label + "> ?o ."
        //     + "}";

        Triple tripleRDFSLabel = Triple.create(
                    Var.alloc("s"),
                    RDFS.label.asNode(),
                    Var.alloc("o")
                    );
        Triple tripleSubClassOf = Triple.create(
                    Var.alloc("s"),
                    RDFS.subClassOf.asNode(),
                    node_URI
                    );
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleSubClassOf);
        basicPattern.add(tripleRDFSLabel);
        Op op = new OpBGP(basicPattern);

        // Works only for SELECT clause, not CONSTRUCT clause:
        // Syntax very fat. You could instead use:
        // op = new OpProject(op,
        //             Arrays.asList(
        //                 new Var[] {
        //                     Var.alloc("s"),
        //                     Var.alloc(RDFS.label.asNode()),
        //                     Var.alloc("o")
        //                 }
        //             )
        //         );

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);

        queryAlgebraBuild.setQueryConstructType();

        BasicPattern basicPatternConstructClause = new BasicPattern();
        basicPatternConstructClause.add(tripleRDFSLabel);
        queryAlgebraBuild.setConstructTemplate(
                new Template(basicPatternConstructClause));

        log.debug("listSubClassesOf\n" + queryAlgebraBuild);

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);
        return m.listSubjects().toList();
    }

}
