package fr.uga.julioju.sempic.Exceptions;

import org.apache.jena.graph.Node_URI;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class FusekiSubjectNotFoundException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    // private Node_URI node_URI;

    public FusekiSubjectNotFoundException(String message) {
        super(ErrorConstantsSempic.FUSEKI_SUBJECT_NOT_FOUND,
                message,
                Status.NOT_FOUND);
    }

    public FusekiSubjectNotFoundException(Node_URI node_URI) {
        super(ErrorConstantsSempic.FUSEKI_SUBJECT_NOT_FOUND,
                "'" + node_URI.getURI() + "' not found in database"
                + " (at least not a RDF subject)." ,
                Status.NOT_FOUND);
        // FIXME UNCOMMENT FOLLOWING CAN'T WORK, DON'T KNOW WHY, SEE README.MD
        // this.node_URI = node_URI;
    }

    // public Node_URI getNode_URI() {
    //     return node_URI;
    // }

}

