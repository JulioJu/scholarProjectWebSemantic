package fr.uga.julioju.sempic;

import java.util.List;

import org.apache.jena.graph.NodeFactory;
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

import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;

/**
 *
 * @author https://github.com/JulioJu
 * TODO factorize
 */
public class RDFStore {

    private static final Logger log = LoggerFactory.getLogger(RDFStore.class);

    /**
     * Delete all the statements where the URI appears as subject or object
     * @param class the URI clas to be deleted (the class cannot be annonymous)
     */
    public static void deleteClassUri(String uriClass) {

            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + uriClass + "> ?p ?o }");
            // this.cnxUpdate("DELETE WHERE { ?uriClass ?p <" + uri + "> }");

            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        NodeFactory.createURI(uriClass),
                        Var.alloc("p"),
                        Var.alloc("o"))
            );
            Update u1 = new UpdateDeleteWhere(acc);
            QuadAcc acc2 = new QuadAcc();
            acc2.addTriple(
                    new Triple(
                        Var.alloc("s"),
                        Var.alloc("p"),
                        NodeFactory.createURI(uriClass))
            );
            Update u2 = new UpdateDeleteWhere(acc2);
            UpdateRequest updateRequest = new UpdateRequest(u1).add(u2);
            log.debug("deleteClassUri\n" + updateRequest.toString());

            FusekiServerConn.serverRestart();

            RDFConn.cnxUpdateRequest(updateRequest);
    }

    // Reference https://users.jena.apache.narkive.com/dMOMKIO8/sparql-to-check-if-a-specific-uri-exists
    public static void testIfUriIsClass(String uri) {
        // String s = "ASK WHERE {"
        //     + "{ <" + uri + "> ?p ?o . }"
        //     + " UNION "
        //     + "{?s ?p <" + uri + "> . }"
        //     + "}";

        Triple tripleSubject = Triple.create(
                    NodeFactory.createURI(uri),
                    Var.alloc("p"),
                    Var.alloc("o"));
        BasicPattern basicPatternSubject = new BasicPattern();
        basicPatternSubject.add(tripleSubject);
        Op opSubject = new OpBGP(basicPatternSubject);

        Triple objectTriple = Triple.create(
                    Var.alloc("s"),
                    Var.alloc("p"),
                    NodeFactory.createURI(uri)
                    );
        BasicPattern basicPatternObject = new BasicPattern();
        basicPatternObject.add(objectTriple);
        Op opObject = new OpBGP(basicPatternObject);

        Op op = OpUnion.create(opSubject, opObject);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryAskType();

        log.debug("testIfUriIsClass\n" + queryAlgebraBuild);

        if (!RDFConn.cnxQueryAsk(queryAlgebraBuild)) {
            throw new FusekiUriNotAClass("'" + uri + "' is not a RDF class");
        }
    }

    public static boolean isUriIsSubject(String uri) {
        Triple triple = Triple.create(
                    NodeFactory.createURI(uri),
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
     * @param uriClass a named class with a complet URI
     *  (the resource cannot be annonymous)
     * @return
     */
    public static List<Resource> listSubClassesOf(String uriClass) {

        RDFStore.testIfUriIsClass(uriClass);

        // String s = "CONSTRUCT { "
        //     + "?s <" + RDFS.label + "> ?o "
        //     + "} WHERE {"
        //     + "?s <" + RDFS.subClassOf + "> <" + uriClass + "> ."
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
                    NodeFactory.createURI(uriClass)
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
