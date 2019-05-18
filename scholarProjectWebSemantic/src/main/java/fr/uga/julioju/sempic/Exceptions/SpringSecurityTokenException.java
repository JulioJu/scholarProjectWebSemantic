package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class SpringSecurityTokenException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public SpringSecurityTokenException() {
        super(ErrorConstantsSempic.SPRING_SECURITY_TOKEN_EXCEPTION,
                "Authorization Token in HTTP header doesn't seem correct."
                , Status.BAD_REQUEST);
    }
}

