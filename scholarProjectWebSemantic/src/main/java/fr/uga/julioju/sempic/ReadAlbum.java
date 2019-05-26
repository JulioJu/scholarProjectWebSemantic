package fr.uga.julioju.sempic;

import java.util.List;
import java.util.Optional;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadAlbum extends AbstractRead {

    private static final Logger log =
        LoggerFactory.getLogger(ReadAlbum.class);

    /** Test if user logged has permissions to manage albumRDF */
    public static void testUserLoggedPermissions(AlbumRDF albumRDF,
            boolean isReadAccess) {
        Optional<AlbumRDF> albumRDFOptional = Optional.empty();
        if (isReadAccess) {
            albumRDFOptional = Optional.of(albumRDF);
        }
        ReadUser.testUserLoggedPermissions(
                "He is not the owner of the albumRDF with the id '"
                + albumRDF.getId()
                + "'. Furthermore this album is not shared with the current user."
                , albumRDF.getOwnerLogin()
                , albumRDFOptional
        );
    }

    private static Model read(long id) {
        String uri = Namespaces.getAlbumUri(id);
        return AbstractRead.read((Node_URI) NodeFactory.createURI(uri));
    }

    public static AlbumRDF readAlbum(Long id) {
        log.debug("REST request to get albumRDF : {}", id);
        Model model = ReadAlbum.read(id);
        String albumOwnerId =
            model.listObjectsOfProperty(SempicOnto.albumOwnerLogin)
            .toList().get(0).toString();
        albumOwnerId = albumOwnerId.substring(albumOwnerId.lastIndexOf('/')+1);
        List<RDFNode> sharedWithRdfNode =
            model.listObjectsOfProperty(SempicOnto.albumSharedWith)
            .toList();
        String[] sharedWithLogin =
            sharedWithRdfNode.stream()
            .map(rdfNode -> {
                String sharedWith = rdfNode.toString();
                sharedWith =
                    sharedWith.substring(sharedWith.lastIndexOf('/') + 1);
                return sharedWith;
            })
            .toArray(String[]::new);
        AlbumRDF albumRDF = new AlbumRDF(id, albumOwnerId, sharedWithLogin);
        return albumRDF;
    }
}
