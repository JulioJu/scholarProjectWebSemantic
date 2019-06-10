package fr.uga.julioju.sempic;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Bound;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.path.P_ZeroOrMore1;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.vocabulary.RDF;

import fr.uga.julioju.sempic.entities.AlbumRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadAlbum extends ReadAbstract {

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

    private static Op createWhereClause(
        BasicPattern basicPatternAlbumWhereClause,
        BasicPattern basicPatternList,
        Optional<Node> albumSharedWith
    ) {

        // Path path = PathParser.parse("rdf:rest*", PrefixMapping.Standard) ;
        Path path = new P_ZeroOrMore1(PathFactory.pathLink(RDF.rest.asNode()));
        TriplePath triplePath = new TriplePath(
                Var.alloc("albumSharedWithList"),
                path,
                Var.alloc("listRest"));
        Op opBGPAlbum = new OpBGP(basicPatternAlbumWhereClause);
        Op opPath = new OpPath(triplePath);
        Op opBGPList = new OpBGP(basicPatternList);
        Op opOptional = OpSequence.create(opPath, opBGPList);
        Op op = OpLeftJoin.createLeftJoin(opBGPAlbum, opOptional, null);

        if (albumSharedWith.isPresent()) {
            Expr exprBound = new E_Bound(new ExprVar("listRest"));
            Expr exprOneOfIn = new E_OneOf(
                    new ExprVar("head"),
                    new ExprList(
                        Arrays.asList(new Expr[] {
                            new NodeValueNode(albumSharedWith.get()),
                        })
                    )
            );
            Expr exprFilter = new E_LogicalAnd(exprBound, exprOneOfIn);
            // Filter that pattern with our expression
            op = OpFilter.filter(exprFilter, op);
        }
        return op;
    }

    /**
      * Retrieve one, zero or several AlbumRDF. SPARQL request
      * depends of type of parameters:
      * <p>
      * In case of @param ownerNode has type {@link Var}
      *     and @param albumNode is {@link Var}
      *     and @param albumSharedWith is {@link Optional}.isEmpty()
      *         In this case retrieve all albums saved in the database.
      * <p>
      * In case of @param ownerNode has type {@link Node_URI}
      *     and @param albumNode is {@link Var}
      *     and @param albumSharedWith is {@link Optional}.isEmpty()
      *         In this case request all albums owned by the user
      *         ownerNode.getURI() .
      *         (zero or one or several AlbumRDF is retrieved).
      * <p>
      * In case of @param ownerNode has type {@link Var}
      *     and @param albumNode is {@link Node_URI}
      *     and @param albumSharedWith is {@link Optional}.isEmpty()
      *         In this case request only one album albumNode.getURI()
      *         in database
      *         (zore or only one AlbumRDF is retrieved).
      * <p>
      * In case of @param ownerNode has type {@link Var}
      *     and @param albumNode is {@link Var}
      *     and @param albumSharedWith is {@link Optional}.isPresent()
      *         In this case request all albums shared with the user
      *         ownerNode.getURI()
      * <p>
      * In case of @param ownerNode has type {@link Node_URI}
      *     and @param albumNode is {@link Var}
      *     and @param albumSharedWith is {@link Optional}.isPresent()
      *         In this case request all albums own by and shared with the user
      *         ownerNode.getURI() (triggered sparql UNION request).
      *         (zero or one or several AlbumRDF is retrieved).
      * <p>
      */
    private static Model read(
            Node albumNode,
            Node ownerNode,
            Optional<Node> albumSharedWith
    ) {
        // CONSTRUCT and WHERE clauses preparation
        Triple tripleAlbumOwnerLogin = Triple.create(
                albumNode,
                SempicOnto.albumOwnerLogin.asNode(),
                Var.alloc("albumOwnerLogin"));
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
        BasicPattern basicPatternAlbumWhereClause =
            new BasicPattern(basicPatternAlbum);
        if (ownerNode instanceof Node_URI) {
            Triple tripleAlbumOwnerLoginSearch = Triple.create(
                    albumNode,
                    SempicOnto.albumOwnerLogin.asNode(),
                    ownerNode);
            basicPatternAlbumWhereClause.add(tripleAlbumOwnerLoginSearch);
        }

        Op op;
        if (ownerNode instanceof Node_URI && albumSharedWith.isPresent()) {
            op = OpUnion.create (
                    ReadAlbum.createWhereClause(
                        basicPatternAlbumWhereClause,
                        basicPatternList,
                        Optional.empty()
                    ),
                    ReadAlbum.createWhereClause(
                        basicPatternAlbum,
                        basicPatternList,
                        albumSharedWith
                    )
                );
        } else {
            op = ReadAlbum.createWhereClause(
                    basicPatternAlbumWhereClause,
                    basicPatternList,
                    albumSharedWith
                    );
        }

        // Prepare CONSTRUCT clause
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.addAll(basicPatternAlbum);
        basicPattern.addAll(basicPatternList);

        // Trigger request
        return ReadAbstract.read((Node_URI) RDF.nil.asNode(), basicPattern, op);
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

    private static List<AlbumRDF> parseModelWithSeveralAlbums(Model model) {
        List<AlbumRDF> albumsRDF = new ArrayList<AlbumRDF>();
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
                albumsRDF.add(oneAlbum);
                // modelWithOnlyOneAlbum.write(System.out, "turtle");

            });
        return albumsRDF;
    }

    /**
      *         In this case retrieve all albums saved in the database.
      */
    public static List<AlbumRDF> readAllAlbums() {
        Var varAlbum = Var.alloc("album");
        Var albumOwnerLogin = Var.alloc("albumOwnerLogin");
        Model model = ReadAlbum.read(
                varAlbum,
                albumOwnerLogin,
                Optional.empty()
        );
        return ReadAlbum.parseModelWithSeveralAlbums(model);
    }

    /**
      *         In this case request all albums owned by the user
      *         with @param login
      *         (zero or one or several AlbumRDF is retrieved).
      */
    public static List<AlbumRDF> readAlbumsOfAnUser(String login) {
        Var varAlbum = Var.alloc("album");
        String ownerURI = Namespaces.getUserUri(login);
        Node_URI node_URIOwner = (Node_URI) NodeFactory.createURI(ownerURI);
        Model model = ReadAlbum.read(varAlbum, node_URIOwner, Optional.empty());
        return ReadAlbum.parseModelWithSeveralAlbums(model);
    }

    /**
      *         In this case request only one album with
      *         @param id in database
      *         (zore or only one AlbumRDF is retrieved).
      *         See README.md section « WITH CONSTRUCT »
      */
    public static AlbumRDF readAlbum(Long id) {
        String albumURI = Namespaces.getAlbumUri(id);
        Node_URI node_URIAlbum = (Node_URI) NodeFactory.createURI(albumURI);
        Var albumOwnerLogin = Var.alloc("albumOwnerLogin");
        Model model =
            ReadAlbum.read(node_URIAlbum, albumOwnerLogin, Optional.empty());
        return parseModelWithOnlyOneAlbumRDF(model, id);
    }

    /**
      *         In this case request all albums shared with the user @param login
      *         (zero or one or several AlbumRDF is retrieved).
      */
    public static List<AlbumRDF> readAlbumsSharedWithAnUser(String login) {
        Var varAlbum = Var.alloc("album");
        Var albumOwnerLogin = Var.alloc("albumOwnerLogin");
        String albumSharedWithUri = Namespaces.getUserUri(login);
        Node_URI node_URIAlbumSharedWith =
            (Node_URI) NodeFactory.createURI(albumSharedWithUri);
        Model model = ReadAlbum.read(
                varAlbum,
                albumOwnerLogin,
                Optional.of(node_URIAlbumSharedWith)
        );
        return ReadAlbum.parseModelWithSeveralAlbums(model);
    }

    /**
      *         In this case request all albums own by and shared with the user
      *         the user @param login
      *         (triggered sparql UNION request).
      *         (zero or one or several AlbumRDF is retrieved).
      */
    public static List<AlbumRDF> readAlbumsOwnedByAndSharedWith(String login) {
        Var varAlbum = Var.alloc("album");
        String albumSharedWithUri = Namespaces.getUserUri(login);
        Node_URI nodeUser =
            (Node_URI) NodeFactory.createURI(albumSharedWithUri);
        Model model = ReadAlbum.read(
                varAlbum,
                nodeUser,
                Optional.of(nodeUser)
        );
        return ReadAlbum.parseModelWithSeveralAlbums(model);
    }

}
