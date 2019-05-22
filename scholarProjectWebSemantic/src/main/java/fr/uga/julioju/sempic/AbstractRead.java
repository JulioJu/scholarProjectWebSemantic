package fr.uga.julioju.sempic;

import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;

public abstract class AbstractRead  {

    private static final Logger log =
        LoggerFactory.getLogger(AbstractRead.class);

    protected static Model read(Node_URI node_URI) {

        Triple tripleUri = Triple.create(
                    node_URI,
                    Var.alloc("p"),
                    Var.alloc("o"));

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleUri);

        // Java API 2) Algebra form of the query
        // —————————————————————

        Op op = new OpBGP(basicPattern);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryConstructType();

        queryAlgebraBuild.setConstructTemplate(new Template(basicPattern));

        log.debug("queryAlgebraBuild\n" + queryAlgebraBuild);

        // Execution
        // —————————

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);

        // Print and tests
        // ———————————————
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        m.write(System.out, "turtle");
        if (m.isEmpty()) {
            throw new FusekiSubjectNotFoundException(node_URI);
        }
        return m;
    }

}
