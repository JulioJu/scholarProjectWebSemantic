package fr.uga.julioju.sempic;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadAlbum extends AbstractRead {

    private static final Logger log =
        LoggerFactory.getLogger(ReadAlbum.class);

    /** Test if user logged has permissions to manage album */
    public static void testUserLoggedPermissions(AlbumRDF album) {
        ReadUser.testUserLoggedPermissions(
                "He is not the owner of the album with the id '"
                + album.getId()
                + "'"
                , album.getOwnerLogin()
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
        AlbumRDF albumRDF = new AlbumRDF(id, albumOwnerId);
        return albumRDF;
    }
}
