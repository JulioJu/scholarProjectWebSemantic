package fr.uga.julioju.sempic.Exceptions;

import org.apache.jena.rdfconnection.RDFConnection;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiDownException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiDownException(RDFConnection conn, String message) {
        super(ErrorConstantsSempic.FUSEKI_DOWN,
                "Connection refused. Fusiki server is probably down ("
                + message + ")",
                Status.SERVICE_UNAVAILABLE);
        conn.end();
    }
}
