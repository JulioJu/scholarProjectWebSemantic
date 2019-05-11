package fr.uga.julioju.sempic;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.julioju.sempic.Exceptions.FusekiUriNotAClass;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

/**
 *
 * @author https://github.com/JulioJu
 * TODO factorize
 */
public class RDFStore {

    private static final Logger log = LoggerFactory.getLogger(RDFStore.class);

    /**
     * Delete all the statements where the URI appears as subject or object
     * @param class the URI clas to be deleted (the class cannot be annonymous)
     */
    public static void deleteClassUri(String uriClass) {

            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + uriClass + "> ?p ?o }");
            // this.cnxUpdate("DELETE WHERE { ?uriClass ?p <" + uri + "> }");

            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(
                    new Triple(
                        NodeFactory.createURI(uriClass),
                        Var.alloc("p"),
                        Var.alloc("o"))
            );
            Update u1 = new UpdateDeleteWhere(acc);
            QuadAcc acc2 = new QuadAcc();
            acc2.addTriple(
                    new Triple(
                        Var.alloc("s"),
                        Var.alloc("p"),
                        NodeFactory.createURI(uriClass))
            );
            Update u2 = new UpdateDeleteWhere(acc2);
            UpdateRequest updateRequest = new UpdateRequest(u1).add(u2);
            log.debug("deleteClassUri\n" + updateRequest.toString());

            RDFConn.cnxUpdateRequest(updateRequest);
    }

    // Reference https://users.jena.apache.narkive.com/dMOMKIO8/sparql-to-check-if-a-specific-uri-exists
    public static void testIfUriIsClass(String uri) {
        // String s = "ASK WHERE {"
        //     + "{ <" + uri + "> ?p ?o . }"
        //     + " UNION "
        //     + "{?s ?p <" + uri + "> . }"
        //     + "}";

        // Only Algebral level. Could be also done at syntax level
        // To do at syntax level,
        // Take model of `this.readPhoto(:id)` and use ElementUnion

        Triple tripleSubject = Triple.create(
                    NodeFactory.createURI(uri),
                    Var.alloc("p"),
                    Var.alloc("o"));
        BasicPattern basicPatternSubject = new BasicPattern();
        basicPatternSubject.add(tripleSubject);
        Op opSubject = new OpBGP(basicPatternSubject);

        Triple objectTriple = Triple.create(
                    Var.alloc("s"),
                    Var.alloc("p"),
                    NodeFactory.createURI(uri)
                    );
        BasicPattern basicPatternObject = new BasicPattern();
        basicPatternObject.add(objectTriple);
        Op opObject = new OpBGP(basicPatternObject);

        Op op = OpUnion.create(opSubject, opObject);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryAskType();

        log.debug("testIfUriIsClass\n" + queryAlgebraBuild);

        if (!RDFConn.cnxQueryAsk(queryAlgebraBuild)) {
            throw new FusekiUriNotAClass("'" + uri + "' is not a RDF class");
        }
    }

    public static boolean isUriIsSubject(String uri) {
        Triple triple = Triple.create(
                    NodeFactory.createURI(uri),
                    Var.alloc("p"),
                    Var.alloc("o"));
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(triple);
        Op op = new OpBGP(basicPattern);
        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryAskType();
        log.debug("isUriIsSubject\n" + queryAlgebraBuild);
        boolean result = RDFConn.cnxQueryAsk(queryAlgebraBuild);
        return result;
    }

    /**
     * Retieves all the resources that are subclasses of resource c. To be
     * selected classes must have the property rdfs:label instanciated
     *
     * @param uriClass a named class with a complet URI
     *  (the resource cannot be annonymous)
     * @return
     */
    public static List<Resource> listSubClassesOf(String uriClass) {

        RDFStore.testIfUriIsClass(uriClass);

        // String s = "CONSTRUCT { "
        //     + "?s <" + RDFS.label + "> ?o "
        //     + "} WHERE {"
        //     + "?s <" + RDFS.subClassOf + "> <" + uriClass + "> ."
        //     + "?s <" + RDFS.label + "> ?o ."
        //     + "}";

        Triple tripleRDFSLabel = Triple.create(
                    Var.alloc("s"),
                    RDFS.label.asNode(),
                    Var.alloc("o")
                    );
        Triple tripleSubClassOf = Triple.create(
                    Var.alloc("s"),
                    RDFS.subClassOf.asNode(),
                    NodeFactory.createURI(uriClass)
                    );
        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(tripleSubClassOf);
        basicPattern.add(tripleRDFSLabel);
        Op op = new OpBGP(basicPattern);

        // Works only for SELECT clause, not CONSTRUCT clause:
        // Syntax very fat. You could instead use:
        // op = new OpProject(op,
        //             Arrays.asList(
        //                 new Var[] {
        //                     Var.alloc("s"),
        //                     Var.alloc(RDFS.label.asNode()),
        //                     Var.alloc("o")
        //                 }
        //             )
        //         );

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);

        queryAlgebraBuild.setQueryConstructType();

        BasicPattern basicPatternConstructClause = new BasicPattern();
        basicPatternConstructClause.add(tripleRDFSLabel);
        queryAlgebraBuild.setConstructTemplate(
                new Template(basicPatternConstructClause));

        log.debug("listSubClassesOf\n" + queryAlgebraBuild);

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);
        return m.listSubjects().toList();
    }

    /**
     * Query a Photo and retrieve all the direct properties
     * of the photo and if the property are depicts, albumId and
     * ownerId
     * If the object has RDFS.label and RDF.type, retrieve the triple
     *
     * @param id
     * @return
     */
    public static Model readPhoto(long id, boolean shouldPrint) {
        String photoUri = Namespaces.getPhotoUri(id);

        // // SPARQL syntax
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
        //         + "FILTER (?p IN (<" + SempicOnto.depicts + "> ,<" + SempicOnto.albumId + ">,<" + SempicOnto.ownerId + "> )) "
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
                            new NodeValueNode(SempicOnto.depicts.asNode()),
                            new NodeValueNode(SempicOnto.albumId.asNode()),
                            new NodeValueNode(SempicOnto.ownerId.asNode())
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

}
