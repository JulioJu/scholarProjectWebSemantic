package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiDownException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiDownException() {
        super(ErrorConstantsSempic.FUSEKI_DOWN, "Fusiki server is down",
                Status.SERVICE_UNAVAILABLE);
    }
}
