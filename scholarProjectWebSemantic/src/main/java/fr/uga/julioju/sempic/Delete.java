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

import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class Delete  {

    private static final Logger log = LoggerFactory.getLogger(Delete.class);

    public static void deleteSubjectUri(Node_URI node_URI) {
            // SPARQL w3c syntax
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
     * Delete user and its albums and its photos
     */
    public static void deleteUserAndItsAlbumsAndItsPhotos(Node_URI userNode) {
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        userNode,
                        Var.alloc("anyUserProperty"),
                        Var.alloc("anyUserObject")
                    )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("anyAlbumOwnByUserDeleted"),
                        Var.alloc("anyAlbumProperty"),
                        Var.alloc("anyAlbumObject")
                    )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("photoInTheAlbumDeleted"),
                        Var.alloc("anyPhotoProperty"),
                        Var.alloc("anyPhotoObject")
                    )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("photoInTheAlbumDeleted"),
                        SempicOnto.photoInAlbum.asNode(),
                        Var.alloc("anyAlbumOwnByUserDeleted")
                    )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("anyAlbumOwnByUserDeleted"),
                        SempicOnto.albumOwnerLogin.asNode(),
                        userNode
                    )
            );
            Update u = new UpdateDeleteWhere(acc);
            UpdateRequest updateRequest = new UpdateRequest(u);
            log.debug("deleteAlbumsAndItsPhotos\n" + updateRequest.toString());
            RDFConn.cnxUpdateRequest(updateRequest);
    }

    /**
     * Delete albums and its photos
     */
    public static void deleteAlbumsAndItsPhotos(Node_URI albumNode) {
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        albumNode,
                        Var.alloc("anyAlbumProperty"),
                        Var.alloc("anyAlbumObject"))
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("photoInTheAlbumDeleted"),
                        Var.alloc("anyPhotoProperty"),
                        Var.alloc("anyPhotoObject")
                        )
            );
            acc.addTriple(
                    new Triple(
                        Var.alloc("photoInTheAlbumDeleted"),
                        SempicOnto.photoInAlbum.asNode(),
                        albumNode
                        )
            );
            Update u = new UpdateDeleteWhere(acc);
            UpdateRequest updateRequest = new UpdateRequest(u);
            log.debug("deleteAlbumsAndItsPhotos\n" + updateRequest.toString());
            RDFConn.cnxUpdateRequest(updateRequest);
    }

}

