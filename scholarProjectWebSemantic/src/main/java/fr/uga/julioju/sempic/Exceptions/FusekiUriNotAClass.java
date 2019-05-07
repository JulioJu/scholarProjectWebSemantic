package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiUriNotAClass extends AbstractThrowableProblem  {

    private static final long serialVersionUID = 1L;

    public FusekiUriNotAClass(String errorMessage) {
        super(ErrorConstantsSempic.FUSEKI_URI_NOT_A_CLASS, errorMessage,
                Status.NOT_FOUND);
    }
}
