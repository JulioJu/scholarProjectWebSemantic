package fr.uga.julioju.sempic;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.AbstractRDF;
import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.julioju.sempic.entities.UserRDF;
import fr.uga.julioju.sempic.entities.UserRDF.UserGroup;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class CreateResource  {

    private static final Logger log =
        LoggerFactory.getLogger(CreateResource.class);

    /** Only for update / create {@link PhotoRDF} or {@link AlbumRDF} .
      * <p>
      *  {@link UserRDF} has its own method as its update depends of lot of
      *  permission tests
      */
    public static <TResource extends Resource, TEntity extends AbstractRDF>
        ResponseEntity<TEntity>
            createOrUpdate(
                    TResource tResource,
                    TEntity tEntity,
                    Model model
                    // Function<Void, Model> printModelSaved
    ) {
        boolean isUpdate = false;
        if (Ask.isUriIsSubject((Node_URI) tResource.asNode())) {
            isUpdate = true;
        }

        //
        log.debug("BELOW: PRINT MODEL THAT WILL BE SAVED\n—————————————");
        model.write(System.out, "turtle");
        // // print the graph on the standard output
        // log.debug("BELOW: PRINT RESOURCE BEFORE IT SAVED"
        //         + "\n—————————————");
        // tResource.getModel().write(System.out, "turtle");

        // Delete photos before update, otherwise it appends
        log.debug("Delete the entity update, otherwise it appends");

        if (isUpdate) {
            Delete.deleteSubjectUri((Node_URI) tResource.asNode());
        }

        RDFConn.saveModel(model);

        // log.debug("BELOW: PRINT MODEL SAVED\n—————————————");
        // printModelSaved.apply(null).write(System.out, "turtle");


        if (isUpdate) {
            return ResponseEntity.ok().body(tEntity);
        } else {
            return ResponseEntity.status(201).body(tEntity);
        }
    }

    /** Precondition: lot of permissions test */
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

    /** Precondition: tests
      * if user exist and has permission to create this album
      * should be already done.
      * As we are in SPARQL, if user didnt't exist
      * it is created silently, without passwords or login. It exists just
      * as an URI without properties.
      */
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

    /** Precondition: tests
      * if user exist and has permission to create this photo, and if
      * album exists.
      * As we are in SPARQL, if the album didnt't exist
      * it is created silently. It exists just
      * as an URI without properties.
      */
    public static Resource create(
            Model model,
            PhotoRDF photoRDF
    ) {
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

        for (int depictionIndex = 0 ;
                depictionIndex < photoRDF.getDepiction().length ;
                depictionIndex++) {
            String depictionURI = SempicOnto.NS
                + photoRDF.getDepiction()[depictionIndex].getDepiction();
            Resource sempicOntoResource = model.createResource(depictionURI);
            Ask.testIfUriIsClass((Node_URI) sempicOntoResource.asNode());

            Resource descriptionResource = model.createResource();

            for (int literalsIndex = 0 ;
                    literalsIndex <
                        photoRDF.getDepiction()[depictionIndex]
                            .getLiterals().length ;
                    literalsIndex++
            ) {
                descriptionResource.addProperty(RDF.type, sempicOntoResource);
                descriptionResource.addLiteral(RDFS.label,
                        photoRDF.getDepiction()[depictionIndex]
                        .getLiterals()[literalsIndex]);
            }
            model.add(photoResource, SempicOnto.photoDepicts, descriptionResource);
        }

        return photoResource;
    }

}
