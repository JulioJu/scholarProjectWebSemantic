package fr.uga.julioju.sempic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import fr.uga.julioju.jhipster.service.UserService;
import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class CreateResource  {

    public static long getUserIdLogged(UserService userService) {
        return userService.getUserWithAuthorities().get().getId();
    }

    public static Resource createUserLogged(Model model,
            UserService userService) {
        return model.createResource(
            Namespaces.getUserUri(CreateResource.getUserIdLogged(userService)),
            SempicOnto.Owner
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
            PhotoRDF photoRDF,
            UserService userService
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
            CreateResource.create(
                model,
                new AlbumRDF(
                    photoRDF.getAlbumId(),
                    CreateResource.getUserIdLogged(userService)
                )
            )
        );
        return photoResource;
    }

}
