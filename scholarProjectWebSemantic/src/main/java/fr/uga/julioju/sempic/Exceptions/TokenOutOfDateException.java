package fr.uga.julioju.sempic.Exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class TokenOutOfDateException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public TokenOutOfDateException(String outdatedRole) {
        super(ErrorConstantsSempic.TOKEN_OUT_OF_DATE_EXCEPTION,
                "The Spring Security role hash in the token "
                + "sent by client is not the same as the role saved "
                + "in the database. Use an other token "
                + "(logout then login again). Note: the token has saved the "
                + "role '" + outdatedRole + "'",
                Status.CONFLICT);
    }
}

