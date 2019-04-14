package fr.uga.julioju.sempic.Exceptions;

import java.net.URI;

public final class ErrorConstantsSempic {

    private final static String PROBLEM_BASE_URL_SEMPIC =
        "https://github.com/JulioJu/scholarProjectWebSemantic";

    public static final URI FUSEKI_DOWN = URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fusiki-down");

}
