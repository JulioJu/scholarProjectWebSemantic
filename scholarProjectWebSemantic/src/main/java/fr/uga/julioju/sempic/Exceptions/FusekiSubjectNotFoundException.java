package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiSubjectNotFoundException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FusekiSubjectNotFoundException(String uri) {
        super(ErrorConstantsSempic.FUSEKI_SUBJECT_NOT_FOUND,
                uri + " not found in database",
                Status.NOT_FOUND);
    }
}

