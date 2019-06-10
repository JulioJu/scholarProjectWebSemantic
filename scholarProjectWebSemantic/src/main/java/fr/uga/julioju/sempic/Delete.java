package fr.uga.julioju.sempic;

import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;

public class Delete  {

    private static final Logger log = LoggerFactory.getLogger(Delete.class);

    public static void deleteSubjectUri(Node_URI node_URI) {
            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + node_URI + "> ?p ?o }");
            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        node_URI,
                        Var.alloc("p"),
                        Var.alloc("o"))
            );
            UpdateRequest updateRequest =
                new UpdateRequest(new UpdateDeleteWhere(acc));
            log.debug("\ndeleteSubjectUri" + updateRequest.toString());
            RDFConn.cnxUpdateRequest(updateRequest);
    }

    /**
     * Delete all the statements where the URI appears as subject or object
     */
    public static void deleteClassUri(Node_URI node_URI) {

            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + node_URI + "> ?p ?o }");
            // this.cnxUpdate("DELETE WHERE { ?node_URI ?p <" + uri + "> }");

            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        node_URI,
                        Var.alloc("p"),
                        Var.alloc("o"))
            );
            Update u1 = new UpdateDeleteWhere(acc);
            QuadAcc acc2 = new QuadAcc();
            acc2.addTriple(
                    new Triple(
                        Var.alloc("s"),
                        Var.alloc("p"),
                        node_URI)
            );
            Update u2 = new UpdateDeleteWhere(acc2);
            UpdateRequest updateRequest = new UpdateRequest(u1).add(u2);
            log.debug("deleteClassUri\n" + updateRequest.toString());

            RDFConn.cnxUpdateRequest(updateRequest);
    }

    /**
     * Delete all the statements where the URI appears as subject or object
     * Delete with three levels:
     * When we delete a user, it its albums, and it deletes album's photo
     */
    public static void cascadingDelete(Node_URI node_URI) {

            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        node_URI,
                        Var.alloc("p1"),
                        Var.alloc("o1"))
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s2"),
                        Var.alloc("p2"),
                        Var.alloc("o2")
                        )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s2"),
                        Var.alloc("p3"),
                        node_URI
                        )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("s3"),
                        Var.alloc("p4"),
                        Var.alloc("s2")
                        )
            );
            Update update = new UpdateDeleteWhere(acc);
            UpdateRequest updateRequest = new UpdateRequest(update);
            log.debug("cascadingDelete\n" + updateRequest.toString());

            RDFConn.cnxUpdateRequest(updateRequest);
    }

    public static void cascadingDeleteWithTests(Node_URI node_URI) {
        String uri = node_URI.getURI();
        if (!Ask.isUriIsSubject(node_URI)) {
            throw new FusekiSubjectNotFoundException(node_URI);
        }
        log.debug("Uri '" + uri + "' exists"
                + ", this RDF subject will be deleted.");

        Delete.cascadingDelete(node_URI);

        if (Ask.isUriIsSubject(node_URI)) {
            throw new FusekiUriNotAClass("Uri '" + uri
                    + "' not deleted.");
        }
        log.debug("Now uri '" + uri + "' doesn't exist as subject"
                + ", therefore it was deleted (as subject).");
    }


}

