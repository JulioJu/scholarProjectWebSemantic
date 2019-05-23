package fr.uga.julioju.jhipster.SempicRest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.CreateResource;
import fr.uga.julioju.sempic.Namespaces;
import fr.uga.julioju.sempic.RDFConn;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ReadPhoto;
import fr.uga.julioju.sempic.entities.PhotoDepictionAnonRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

/**
 * REST controller for managing PhotoRDFResource.
 */
@RestController
@RequestMapping("/api")
public class PhotoRDFResource {

    private final Logger log = LoggerFactory.getLogger(PhotoRDFResource.class);

    /**
     * {@code PUT  /photoRDF} : Creates or Updates a photoRDF
     *
     * @param photoRDF the photoRDF to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoRDF,
     * or with status {@code 400 (Bad Request)} if the photoRDF is not valid,
     * or with status {@code 500 (Internal Server Error)} if the photoRDF couldn't be updated.
     */
    @PutMapping("/photoRDF")
    public ResponseEntity<PhotoRDF> createOrUpdate(
            @Valid @RequestBody PhotoRDF photoRDF) {
        log.debug("REST request to update PhotoRDF : {}", photoRDF);

        Model model = ModelFactory.createDefaultModel();

        Resource photoResource = CreateResource.create(
                model,
                photoRDF
                );

        // Delete photos before update, otherwise it appends
        log.debug("Delete photos before update, otherwise it appends");
        RDFStore.deleteClassUri((Node_URI) photoResource.asNode());

        // TODO use bag
        for (int depictionIndex = 0 ;
                depictionIndex < photoRDF.getDepiction().length ;
                depictionIndex++) {
            String depictionURI = SempicOnto.NS
                + photoRDF.getDepiction()[depictionIndex].getDepiction();
            Resource sempicOntoResource = model.createResource(depictionURI);
            RDFStore.testIfUriIsClass((Node_URI) sempicOntoResource.asNode());

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
            model.add(photoResource, SempicOnto.depicts, descriptionResource);
        }

        log.debug("BELOW: MODEL BEFORE IT SAVED\n—————————————");
        model.write(System.out, "turtle");

        // print the graph on the standard output
        log.debug("BELOW: PRINT RESOURCE BEFORE IT SAVED"
                + "\n—————————————");
        photoResource.getModel().write(System.out, "turtle");

        RDFConn.saveModel(model);
        log.debug("BELOW: PRINT MODEL SAVED\n—————————————");
        ReadPhoto.read(photoRDF.getId(), true)
            .write(System.out, "turtle");

        return ResponseEntity.ok().body(photoRDF);
    }

    /**
     * {@code GET  /photoRDF/:id} : get the "id" photoRDF.
     *
     * @param id the id of the photoRDF to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the photoRDF, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/photoRDF/{id}")
    public ResponseEntity<PhotoRDF> getPhoto(@PathVariable Long id) {
        log.debug("REST request to get photoRDF : {}", id);
        Model model = ReadPhoto.read(id, true);
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        model.write(System.out, "turtle");
        if (model.isEmpty()) {
            log.error("PhotoRDF with uri '"
                    + Namespaces.getPhotoUri(id)
                    + "' doesn't exist in Fuseki Database"
                    + " (at least not a RDF subject).");
            return ResponseEntity.notFound().build();
        }
        String albumIdWithDatatype =
                model.listObjectsOfProperty(SempicOnto.albumId)
                .toList().get(0).toString();
        long albumId = Integer.parseInt(
                albumIdWithDatatype
                .substring(albumIdWithDatatype.lastIndexOf('/')+1)
                );
        List<Resource> depictObjects =
            model.listObjectsOfProperty(SempicOnto.depicts)
            .toList().parallelStream().map(RDFNode::asResource)
            .collect(Collectors.toList());
        PhotoDepictionAnonRDF[] photoDepictionRDF =
            depictObjects.parallelStream().map(
                r -> {
                    String[] literalResources =
                        model.listObjectsOfProperty(r, RDFS.label).toList()
                        .parallelStream().map(RDFNode::toString)
                        .toArray(String[]::new);
                    // TODO it seams that properties are order from general to
                    // specific
                    // Not know if it's always that.
                    List<RDFNode> properties =
                        model.listObjectsOfProperty(r, RDF.type).toList();
                    return new PhotoDepictionAnonRDF(
                        properties.get(properties.size() - 1).toString(),
                        literalResources
                    );
                }
            ).toArray(PhotoDepictionAnonRDF[]::new);
        PhotoRDF photoRDF = new PhotoRDF(
                id,
                albumId,
                photoDepictionRDF
                );
        return ResponseEntity.ok().body(photoRDF);
    }

    /**
     * {@code DELETE  /photoRDF/:id} : delete the "id" photoRDF.
     *
     * @param id the id of the photoRDF to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/photoRDF/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        log.debug("REST request to delete photoRDF : {}", id);
        String uri = Namespaces.getPhotoUri(id);
        Node_URI node_URI = (Node_URI) NodeFactory.createURI(uri);
        RDFStore.deleteClassUriWithTests(node_URI);
        return ResponseEntity.noContent().build();
    }

}
