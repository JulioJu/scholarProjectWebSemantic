package fr.uga.julioju.sempic;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class CreateResource  {

    public static Resource create(Model model, UserRDF userRDF) {
        Resource userGroupRes = SempicOnto.NormalUserGroup;
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            userGroupRes = SempicOnto.AdminGroup;
        }

        Resource userRessource = model.createResource(
            Namespaces.getUserUri(userRDF.getLogin()),
            userGroupRes
        );

        userRessource.addLiteral(SempicOnto.usersPassword,
                userRDF.getPassword());

        return userRessource;
    }

    public static Resource create(Model model, AlbumRDF albumRDF) {
        Resource albumRDFResource = model.createResource(
                Namespaces.getAlbumUri(albumRDF.getId()),
                SempicOnto.Album
        );
        albumRDFResource.addProperty(SempicOnto.albumOwnerLogin,
                model.createResource(
                    Namespaces.getUserUri(albumRDF.getOwnerLogin())
                )
        );
        for (String user : albumRDF.getSharedWith()) {
            String sharedWithUri = Namespaces.getUserUri(user);
            var node_URI = (Node_URI) NodeFactory.createURI(sharedWithUri);
            if (RDFStore.isUriIsSubject(node_URI)) {
                albumRDFResource.addProperty(SempicOnto.albumSharedWith,
                        model.createResource(sharedWithUri)
                );
            } else {
                throw new FusekiSubjectNotFoundException(node_URI);
            }
        }
        return albumRDFResource;
    }

    /** Precondition: album should exist */
    public static Resource create(
            Model model,
            PhotoRDF photoRDF
    ) {
        // String albumURI = Namespaces.getAlbumUri(photoRDF.getAlbumId());
        // Node_URI node_URI = (Node_URI) NodeFactory.createURI(albumURI);
        // if (!RDFStore.isUriIsSubject(node_URI)) {
        //     throw new FusekiSubjectNotFoundException(node_URI);
        // }
        Resource photoResource = model.createResource(
                Namespaces.getPhotoUri(photoRDF.getId()),
                SempicOnto.Photo
        );
        photoResource.addProperty(
            SempicOnto.albumId,
            model.createResource(
                    Namespaces.getAlbumUri(photoRDF.getAlbumId()),
                    SempicOnto.Album
                )
        );
        return photoResource;
    }

}
