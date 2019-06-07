package fr.uga.julioju.sempic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.path.P_ZeroOrMore1;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.vocabulary.RDF;
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

    /** Either albumNode or ownerNode should be on type {@link Node_URI}  */
    private static Model read(Node albumNode, Node ownerNode) {
        // CONSTRUCT and WHERE clauses preparation
        Triple tripleAlbumOwnerLogin = Triple.create(
                albumNode,
                SempicOnto.albumOwnerLogin.asNode(),
                ownerNode);
        Triple tripleAlbumSTitle = Triple.create(
                albumNode,
                SempicOnto.albumSTitle.asNode(),
                Var.alloc("albumSTitle"));
        Triple tripleAlbumSharedWith = Triple.create(
                albumNode,
                SempicOnto.albumSharedWith.asNode(),
                Var.alloc("albumSharedWithList"));
        Triple tripleListFirst = Triple.create(
                Var.alloc("listRest"),
                RDF.first.asNode(),
                Var.alloc("head"));
        Triple tripleListRest = Triple.create(
                Var.alloc("listRest"),
                RDF.rest.asNode(),
                Var.alloc("tail"));
        BasicPattern basicPatternAlbum = new BasicPattern();
        basicPatternAlbum.add(tripleAlbumOwnerLogin);
        basicPatternAlbum.add(tripleAlbumSTitle);
        basicPatternAlbum.add(tripleAlbumSharedWith);
        BasicPattern basicPatternList = new BasicPattern();
        basicPatternList.add(tripleListFirst);
        basicPatternList.add(tripleListRest);

        // Prepare WHERE clause
        // Path path = PathParser.parse("rdf:rest*", PrefixMapping.Standard) ;
        Path path = new P_ZeroOrMore1(PathFactory.pathLink(RDF.rest.asNode()));
        TriplePath triplePath = new TriplePath(
                Var.alloc("albumSharedWithList"),
                path,
                Var.alloc("listRest"));
        Op opBGPAlbum = new OpBGP(basicPatternAlbum);
        Op opPath = new OpPath(triplePath);
        Op opBGPList = new OpBGP(basicPatternList);
        Op opOptional = OpSequence.create(opPath, opBGPList);
        Op op = OpLeftJoin.createLeftJoin(opBGPAlbum, opOptional, null);

        // Prepare CONSTRUCT clause
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.addAll(basicPatternAlbum);
        basicPattern.addAll(basicPatternList);

        if (albumNode instanceof Node_URI) {
            Node_URI node_URIAlbum = (Node_URI) albumNode;
            return AbstractRead.read(node_URIAlbum, basicPattern, op);
        } else {
            Node_URI node_URIOwner = (Node_URI) ownerNode;
            return AbstractRead.read(node_URIOwner, basicPattern, op);
        }

    }

    /** See README.md section « WITH CONSTRUCT » */
    private static Model readOnAlbum(long id) {
        log.debug("REST request to get albumRDF : {}", id);
        String albumURI = Namespaces.getAlbumUri(id);
        Node_URI node_URIAlbum = (Node_URI) NodeFactory.createURI(albumURI);
        Var albumOwnerLogin = Var.alloc("albumOwnerLogin");
        return ReadAlbum.read(node_URIAlbum, albumOwnerLogin);
    }

    private static Model readAlbumSOwner(String login) {
        String ownerURI = Namespaces.getUserUri(login);
        Node_URI node_URIOwner = (Node_URI) NodeFactory.createURI(ownerURI);
        Var varAlbum = Var.alloc("album");
        return ReadAlbum.read(varAlbum, node_URIOwner);
    }

    private static AlbumRDF parseModelWithOnlyOneAlbumRDF(Model model,
            Long id
    ) {
        String title =
            model.listObjectsOfProperty(SempicOnto.albumSTitle)
            .toList().get(0).toString();
        String albumOwnerId =
            model.listObjectsOfProperty(SempicOnto.albumOwnerLogin)
            .toList().get(0).toString();
        albumOwnerId = albumOwnerId.substring(albumOwnerId.lastIndexOf('/')+1);
        Optional<RDFNode> sharedWithRdfNode =
            model
            .listObjectsOfProperty(SempicOnto.albumSharedWith)
            .nextOptional();
        String[] sharedWithLogin = null;
        if (sharedWithRdfNode.isPresent()) {
            sharedWithLogin =
                model.getList(sharedWithRdfNode.get().asResource())
                .asJavaList()
                .stream()
                .map(rdfNode -> {
                    String sharedWith = rdfNode.toString();
                    sharedWith =
                        sharedWith.substring(sharedWith.lastIndexOf('/') + 1);
                    return sharedWith;
                })
                .toArray(String[]::new);
        }
        AlbumRDF albumRDF = new AlbumRDF(
                id,
                title,
                albumOwnerId,
                sharedWithLogin);
        return albumRDF;
    }

    public static AlbumRDF readAlbum(Long id) {
        Model model = ReadAlbum.readOnAlbum(id);
        return parseModelWithOnlyOneAlbumRDF(model, id);
    }

    public static List<AlbumRDF> readAlbumsOfAnUser(String login) {
        Model model = ReadAlbum.readAlbumSOwner(login);
        List<AlbumRDF> albumRDF = new ArrayList<AlbumRDF>();
        model
            .listSubjectsWithProperty(SempicOnto.albumOwnerLogin)
            .toList()
            .forEach(subject -> {
                Model modelWithOnlyOneAlbum = model.query(
                        new SimpleSelector(subject,
                            null,
                            (RDFNode) null
                            )
                );
                Statement resourceListSharedWith = model.getProperty(subject,
                        SempicOnto.albumSharedWith);
                RDFList rdfList = resourceListSharedWith.getList();
                if (rdfList != RDF.nil) {
                    RDFNode[] rdfNodeArray = rdfList
                        .iterator()
                        .toList()
                        .toArray(new RDFNode[rdfList.size()]);
                    RDFList rdfListNewModel =
                        modelWithOnlyOneAlbum.createList(rdfNodeArray);

                    // We should remove the old list before add an other
                    modelWithOnlyOneAlbum
                        .removeAll(subject, SempicOnto.albumSharedWith, null);

                    modelWithOnlyOneAlbum.add(subject,
                            SempicOnto.albumSharedWith,
                            // We can't add simply `rdfList` (underlying Model is
                            // `model` and not `modelWithOnlyOneAlbum`)
                            rdfListNewModel
                    );
                }

                String subjectString = subject.getURI();
                Long id = Long.parseLong(
                        subjectString
                        .substring(subjectString.lastIndexOf('/') + 1)
                        );
                AlbumRDF oneAlbum = ReadAlbum
                    .parseModelWithOnlyOneAlbumRDF(modelWithOnlyOneAlbum, id);
                albumRDF.add(oneAlbum);
                // modelWithOnlyOneAlbum.write(System.out, "turtle");

            });
        return albumRDF;
    }

}
