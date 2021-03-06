package fr.uga.julioju.sempic.Exceptions;

import java.net.URI;

public final class ErrorConstantsSempic {

    private final static String PROBLEM_BASE_URL_SEMPIC =
        "https://github.com/JulioJu/scholarProjectWebSemantic";

    // ERRORS from server
    // —————————————————

    protected static final URI FUSEKI_DOWN = URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-down");

    protected static final URI FUSEKI_JENA_QUERY_EXCEPTION =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-jena-query-exception");

    // ERROR from client
    // ——————————————————

    protected static final URI FUSEKI_SUBJECT_NOT_FOUND =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-subject-not-found");

    protected static final URI FUSEKI_URI_NOT_A_CLASS =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-uri-not-a-class");

    protected static final URI SPRING_SECURITY_TOKEN_EXCEPTION =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/spring-security-token-exception");

    protected static final URI TOKEN_OUT_OF_DATE_EXCEPTION =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/token-out-of-date-exception");
}
