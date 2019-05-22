package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiResourceForbidden extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiResourceForbidden(String errorMessage) {
        super(ErrorConstantsSempic.FUSEKI_RESOURCE_FORBIDDEN, errorMessage,
                Status.FORBIDDEN);
    }
}
