package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class QueryJenaException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public QueryJenaException() {
        super(ErrorConstantsSempic.FUSEKI_DOWN, "Error in Query Jena",
                Status.INTERNAL_SERVER_ERROR);
    }
}

