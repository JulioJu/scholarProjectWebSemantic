package fr.uga.julioju.sempic.Exceptions;

import java.net.URI;

public final class ErrorConstantsSempic {

    private final static String PROBLEM_BASE_URL_SEMPIC =
        "https://github.com/JulioJu/scholarProjectWebSemantic";

    // ERRORS from server
    // —————————————————

    public static final URI FUSEKI_DOWN = URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-down");

    public static final URI FUSEKI_JENA_QUERY_EXCEPTION =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-jena-query-exception");

    // ERROR from client
    // ——————————————————

    public static final URI FUSEKI_SUBJECT_NOT_FOUND =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-subject-not-found");

    public static final URI FUSEKI_UNAUTHORIZED =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-unauthorized");

    public static final URI FUSEKI_URI_NOT_A_CLASS =
        URI.create(PROBLEM_BASE_URL_SEMPIC +
            "/fuseki-uri-not-a-class");
}
