package fr.uga.julioju.sempic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class CreateResource  {

    public static Resource createUserLogged(Model model, UserRDF userRDF) {
        Resource userGroupRes = SempicOnto.NormalUserGroup;
        if (userRDF.getUserGroup().equals(UserGroup.ADMIN_GROUP)) {
            userGroupRes = SempicOnto.AdminGroup;
        }

        return model.createResource(
            Namespaces.getUserUri(userRDF.getLogin()),
            userGroupRes
        );
    }

    public static Resource create(Model model, AlbumRDF albumRDF) {
        return model.createResource(
                Namespaces.getAlbumUri(albumRDF.getId()),
                SempicOnto.Album
        );
    }

    public static Resource create(
            Model model,
            PhotoRDF photoRDF
    ) {
        String albumURI = Namespaces.getAlbumUri(photoRDF.getAlbumId());
        if (!RDFStore.isUriIsSubject(albumURI)) {
            throw new FusekiSubjectNotFoundException(albumURI);
        }
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
