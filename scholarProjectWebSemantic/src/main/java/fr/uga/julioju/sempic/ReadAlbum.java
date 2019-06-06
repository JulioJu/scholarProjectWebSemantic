package fr.uga.julioju.sempic;

import java.util.Optional;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
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

    /** See README.md section « WITH CONSTRUCT » */
    private static Model read(long id) {
        String albumURI = Namespaces.getAlbumUri(id);
        Node_URI node_URIAlbum = (Node_URI) NodeFactory.createURI(albumURI);

        // CONSTRUCT and WHERE clauses preparation
        Triple tripleAlbumOwnerLogin = Triple.create(
                node_URIAlbum,
                SempicOnto.albumOwnerLogin.asNode(),
                Var.alloc("albumOwnerLogin"));
        Triple tripleAlbumSharedWith = Triple.create(
                node_URIAlbum,
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
        basicPatternAlbum.add(tripleAlbumSharedWith);
        basicPatternAlbum.add(tripleAlbumOwnerLogin);
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

        return AbstractRead.read(node_URIAlbum, basicPattern, op);
    }

    public static AlbumRDF readAlbum(Long id) {
        log.debug("REST request to get albumRDF : {}", id);
        Model model = ReadAlbum.read(id);
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
        AlbumRDF albumRDF = new AlbumRDF(id, albumOwnerId, sharedWithLogin);
        return albumRDF;
    }
}
