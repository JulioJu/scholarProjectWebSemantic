package fr.uga.julioju.sempic;

import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;

public class Ask  {

    private static final Logger log = LoggerFactory.getLogger(Ask.class);

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
        if (!Ask.isUriIsClass(node_URI)) {
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


}
