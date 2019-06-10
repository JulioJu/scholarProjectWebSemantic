package fr.uga.julioju.sempic;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fr.uga.julioju.sempic.Ask;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class CreateResource  {

    public static Resource create(Model model, UserRDF userRDF) {
        Resource userGroupRes = SempicOnto.UserGroupNormal;
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            userGroupRes = SempicOnto.UserGroupAdmin;
        }

        Resource userRessource = model.createResource(
            Namespaces.getUserUri(userRDF.getLogin()),
            userGroupRes
        );

        userRessource.addLiteral(SempicOnto.usersPassword,
                userRDF.getPassword());

        return userRessource;
    }

    /** Precondition: user should exist */
    public static Resource create(Model model, AlbumRDF albumRDF) {
        Resource albumRDFResource = model.createResource(
                Namespaces.getAlbumUri(albumRDF.getId()),
                SempicOnto.Album
        );
        albumRDFResource.addProperty(SempicOnto.albumSTitle,
                albumRDF.getTitle()
        );
        albumRDFResource.addProperty(SempicOnto.albumOwnerLogin,
                model.createResource(
                    Namespaces.getUserUri(albumRDF.getOwnerLogin())
                )
        );
        if (albumRDF.getSharedWith() != null) {
            System.out.println(new RDFNode[albumRDF.getSharedWith().length]);
            RDFNode[] rdfListElement = new RDFNode[albumRDF.getSharedWith().length];
            int forLoop = 0;
            for (String user : albumRDF.getSharedWith()) {
                String sharedWithUri = Namespaces.getUserUri(user);
                var node_URI = (Node_URI) NodeFactory.createURI(sharedWithUri);
                if (Ask.isUriIsSubject(node_URI)) {
                    rdfListElement[forLoop] =
                        model.createResource(sharedWithUri);
                } else {
                    throw new FusekiSubjectNotFoundException(node_URI);
                }
                forLoop++;
            }
            RDFList rdfList = model.createList(rdfListElement);
            albumRDFResource.addProperty(SempicOnto.albumSharedWith, rdfList);
        } else {
            // Empty list are not persisted, but should not be null
            albumRDFResource.addProperty(SempicOnto.albumSharedWith, RDF.nil);
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
            SempicOnto.photoInAlbum,
            model.createResource(
                    Namespaces.getAlbumUri(photoRDF.getAlbumId()),
                    SempicOnto.Album
                )
        );
        return photoResource;
    }

}
