package fr.uga.julioju.sempic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiSubjectNotFoundException;
import fr.uga.julioju.sempic.entities.PhotoDepictionAnonRDF;
import fr.uga.julioju.sempic.entities.PhotoRDF;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadPhoto  {

    private static final Logger log =
        LoggerFactory.getLogger(ReadPhoto.class);

    /**
     * Query a Photo and retrieve all the direct properties
     * of the photo and if the property are depicts, albumId and
     * albumOwnerId .
     * If the object has RDFS.label and RDF.type, retrieve the triple.
     * <p>
     * « Java API 1): Syntax use of the query » is not used to retrieve
     * the Photo. JUST USED TO SHOW TO THE TEACHER THAT I'VE WORKED ON IT.
     *
     * @param id
     * @return
     */
    public static Model read(long id, boolean shouldPrint) {
        String photoUri = Namespaces.getPhotoUri(id);

        // // SPARQL W3C syntax
        // // —————————————
        // String s = "CONSTRUCT {"
        //         + "<" + photoUri + "> ?p ?o . "
        //         + "?o <" + RDFS.label + "> ?o2 . "
        //         + "?o <" + RDF.type + "> ?o3 . "
        //         + "} "
        //         + "WHERE { "
        //         + "<" + photoUri + "> ?p ?o . "
        //         + "OPTIONAL {"
        //         + "?o <" + RDFS.label + "> ?o2 ."
        //         + "?o <" + RDF.type + "> ?o3 ."
        //         + "}"
        //         + "FILTER (?p IN (<" + SempicOnto.depicts + "> ,<" + SempicOnto.albumId + ">,<" + SempicOnto.albumOwnerId + "> )) "
        //         + "}";

        // Java API
        // ———————
        // ———————

        Triple tripleUri = Triple.create(
                    NodeFactory.createURI(photoUri),
                    Var.alloc("p"),
                    Var.alloc("o"));

        Triple tripleLabel = Triple.create(
                    Var.alloc("o"),
                    RDFS.label.asNode(),
                    Var.alloc("o2"));

        Triple tripleRDFType = Triple.create(
                    Var.alloc("o"),
                    RDF.type.asNode(),
                    Var.alloc("o3"));

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleUri);
        basicPattern.add(tripleLabel);
        basicPattern.add(tripleRDFType);

        BasicPattern basicPatternWhere = new BasicPattern();
        basicPatternWhere.add(tripleUri);

        BasicPattern basicPatterWhereOptional = new BasicPattern();
        basicPatterWhereOptional.add(tripleLabel);
        basicPatterWhereOptional.add(tripleRDFType);

        Expr exprFilter = new E_OneOf(
                new ExprVar("p"),
                new ExprList(
                    Arrays.asList(new Expr[] {
                        new NodeValueNode(SempicOnto.photoDepictsDatatypeProperty.asNode()),
                        new NodeValueNode(SempicOnto.photoInAlbum.asNode()),
                        new NodeValueNode(SempicOnto.albumOwnerLogin.asNode())
                    })
                )
        );

        // Java API 1): Syntax form of the query
        // ———————————————
        // NOT USED, JUST PRINT IN CONSOLE
        // ——————————————
        // See also
        // https://stackoverflow.com/questions/9979931/jena-updatefactory
        if (shouldPrint) {

            Query querySyntaxBuild = new Query();

            // set CONSTRUCT clause
            querySyntaxBuild.setQueryConstructType();
            ElementTriplesBlock elementTriplesBlock = new ElementTriplesBlock();
            elementTriplesBlock.addTriple(tripleUri);
                                                // BasicPattern or QuadAcc
            querySyntaxBuild.setConstructTemplate(new Template(basicPattern));

            // set WHERE clause
            ElementGroup elementGroup = new ElementGroup();
            elementGroup.addTriplePattern(tripleUri);

            ElementGroup elementGroupOptional = new ElementGroup();
            elementGroupOptional.addTriplePattern(tripleLabel);
            elementGroupOptional.addTriplePattern(tripleRDFType);
            ElementOptional elementOptional = new ElementOptional(
                    elementGroupOptional
                    );
            elementGroup.addElement(elementOptional);

            ElementFilter elementFilter = new ElementFilter(exprFilter);
            elementGroup.addElementFilter(elementFilter);

            querySyntaxBuild.setQueryPattern(elementGroup);

            log.debug("querySyntaxBuild\n" + querySyntaxBuild);
        }

        // Java API 2) Algebra form of the query
        // —————————————————————

        // Make a BGP (Basic Graph Pattern) from this pattern
        // see also
        // https://afia.asso.fr/wp-content/uploads/2018/01/Corby_PDIA2017_RechercheSemantique.pdf
        // to understand what is a BGP.
        Op opLeft = new OpBGP(basicPatternWhere);
        Op opRight = new OpBGP(basicPatterWhereOptional);

        Op op = OpLeftJoin.createLeftJoin(opLeft, opRight, null);

        // Filter that pattern with our expression
        op = OpFilter.filter(exprFilter, op);

        // Notice that the query form (SELECT, CONSTRUCT, DESCRIBE, ASK) isn't
        // part of the algebra, and we have to set this in the query (although
        // SELECT is the default). FROM and FROM NAMED are similarly absent.
        // https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html
        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryConstructType();

        // We could also use `new OpProject(op, Arrays.asList(Var.alloc( ……`
        // if we use a SELECT clause (see the README.md).
        queryAlgebraBuild.setConstructTemplate(new Template(basicPattern));

        if (shouldPrint) {
            log.debug("queryAlgebraBuild\n" + queryAlgebraBuild);
        }

        // Execution
        // —————————
        // Here we execute only queryAlgebraBuild and not querySyntaxBuild

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);
        return m;
    }

    public static PhotoRDF getPhotoById(Long id) {
        Model model = ReadPhoto.read(id, true);
        log.debug("BELOW: PRINT MODEL RETRIEVED\n—————————————");
        model.write(System.out, "turtle");
        if (model.isEmpty()) {
            String uri = Namespaces.getPhotoUri(id);
            throw new FusekiSubjectNotFoundException(
                    (Node_URI) NodeFactory.createURI(uri)
                    );
        }
        String albumIdWithDatatype =
                model.listObjectsOfProperty(SempicOnto.photoInAlbum)
                .toList().get(0).toString();
        long albumId = Integer.parseInt(
                albumIdWithDatatype
                .substring(albumIdWithDatatype.lastIndexOf('/')+1)
                );
        List<Resource> depictObjects =
            model.listObjectsOfProperty(SempicOnto.photoDepictsDatatypeProperty)
            .toList().parallelStream().map(RDFNode::asResource)
            .collect(Collectors.toList());
        PhotoDepictionAnonRDF[] photoDepictionRDF =
            depictObjects.parallelStream().map(
                r -> {
                    String[] literalResources =
                        model.listObjectsOfProperty(r, RDFS.label).toList()
                        .parallelStream().map(RDFNode::toString)
                        .toArray(String[]::new);
                    // TODO it seems that properties are order from general to
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
        return photoRDF;
    }

}

