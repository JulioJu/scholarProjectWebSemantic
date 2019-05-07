package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiUnauthorized extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiUnauthorized(String errorMessage) {
        super(ErrorConstantsSempic.FUSEKI_UNAUTHORIZED, errorMessage,
                Status.UNAUTHORIZED);
    }
}
