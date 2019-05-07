package fr.uga.julioju.sempic.Exceptions;

import org.apache.jena.rdfconnection.RDFConnection;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiJenaQueryException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiJenaQueryException(RDFConnection conn, String detail) {
        super(ErrorConstantsSempic.FUSEKI_JENA_QUERY_EXCEPTION,
                "Error in Query Jena",
                Status.INTERNAL_SERVER_ERROR, detail);
        conn.end();
    }
}

