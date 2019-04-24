package fr.uga.julioju.sempic;

import fr.uga.julioju.sempic.Exceptions.FusekiDownException;
import fr.uga.julioju.sempic.Exceptions.FusekiJenaQueryException;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprNode;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.ExprVisitor;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.graph.NodeTransform;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author jerome.david@univ-grenoble-alpes.fr
 */
public class RDFStore {

    public final static String ENDPOINT_QUERY = "http://localhost:3030/sempic/sparql"; // SPARQL endpoint
    public final static String ENDPOINT_UPDATE = "http://localhost:3030/sempic/update"; // SPARQL UPDATE endpoint
    public final static String ENDPOINT_GSP = "http://localhost:3030/sempic/data"; // Graph Store Protocol

    protected final RDFConnection cnx;

    public RDFStore() {
        cnx = RDFConnectionFactory.connect(ENDPOINT_QUERY, ENDPOINT_UPDATE, ENDPOINT_GSP);
    }

    private void cnxCommit() {
        try {
            cnx.commit();
        } catch (HttpException e) {
            throw new FusekiDownException();
        }
    }

    private void cnxUpdateRequest (UpdateRequest u) {
        try {
            cnx.update(u);
        } catch (HttpException e) {
            throw new FusekiDownException();
        }
    }

    private Model cnxQueryConstruct (String s) {
        try {
            return cnx.queryConstruct(s);
        } catch (QueryExceptionHTTP e) {
            throw new FusekiDownException();
        } catch (QueryException e) {
            throw new FusekiJenaQueryException(e.toString());
        }
    }

    private Model cnxQueryConstruct (Query q) {
        try {
            return cnx.queryConstruct(q);
        } catch (QueryExceptionHTTP e) {
            throw new FusekiDownException();
        } catch (QueryException e) {
            throw new FusekiJenaQueryException(e.toString());
        }
    }

    private boolean cnxQueryAsk (String s) {
        try {
            return cnx.queryAsk(s);
        } catch (QueryExceptionHTTP e) {
            throw new FusekiDownException();
        } catch (QueryException e) {
            throw new FusekiJenaQueryException(e.toString());
        }
    }

    /**
     * Save the given model into the triple store.
     * @param m THe Jena model to be persisted
     */
    public void saveModel(Model m) {
        cnx.begin(ReadWrite.WRITE);
        try {
            cnx.load(m);
        } catch (HttpException e) {
            throw new FusekiDownException();
        }
        this.cnxCommit();
    }

    /**
     * Delete all the statements where the resource appears as subject or object
     * @param r The named resource to be deleted (the resource cannot be annonymous)
     */
    public void deleteResource(Resource r) {
        if (r.isURIResource()) {
            cnx.begin(ReadWrite.WRITE);

            // SPARQL syntax
            // —————————————
            // this.cnxUpdate("DELETE WHERE { <" + r.getURI() + "> ?p ?o }");
            // this.cnxUpdate("DELETE WHERE { ?s ?p <" + r.getURI() + "> }");

            // Java
            // —————————————
            QuadAcc acc = new QuadAcc();
            acc.addTriple(new Triple(r.asNode(),
                        Var.alloc("p"),
                        Var.alloc("o")));
            Update u1 = new UpdateDeleteWhere(acc);
            QuadAcc acc2 = new QuadAcc();
            acc2.addTriple(new Triple(r.asNode(),
                        Var.alloc("p"),
                        Var.alloc("o")));
            Update u2 = new UpdateDeleteWhere(acc2);
            UpdateRequest updateRequest = new UpdateRequest(u1).add(u2);
            System.out.println(updateRequest);
            this.cnxUpdateRequest(updateRequest);
            this.cnxCommit();
        }
    }

    // Reference https://users.jena.apache.narkive.com/dMOMKIO8/sparql-to-check-if-a-specific-uri-exists
    public void testIfUriIsClass(String uri) {
        String s = "ASK WHERE {"
            + "{ <" + uri + "> ?p ?o . }"
            + " UNION "
            + "{?s ?p <" + uri + "> . }"
            + "}";
        if (!this.cnxQueryAsk(s)) {
            throw new FusekiJenaQueryException(
                    "'" + uri + "' is not a RDF class");
        }
    }

    // Note: we could use directly string uri, but it would become vulnerable
    // to SQL injection
    /**
     * Retieves all the resources that are subclasses of resource c. To be
     * selected classes must have the property rdfs:label instanciated
     *
     * @param c A named class (the resource cannot be annonymous)
     * @return
     */
    public List<Resource> listSubClassesOf(Resource c) {
        String uri = c.getURI();

        this.testIfUriIsClass(uri);

        String s = "CONSTRUCT { "
            + "?s <" + RDFS.label + "> ?o "
            + "} WHERE {"
            + "?s <" + RDFS.subClassOf + "> <" + uri + "> ."
            + "?s <" + RDFS.label + "> ?o ."
            + "}";
        Model m = this.cnxQueryConstruct(s);
        return m.listSubjects().toList();
    }

    /**
     * Create a list of anonymous instances for each of the classes
     * given as parameter. The created instances have a label "a "+ label of the class.
     * @param classes
     * @return
     */
    public List<Resource> createAnonInstances(List<Resource> classes) {
        Model m = ModelFactory.createDefaultModel();
        List<Resource> res = new ArrayList<>();
        for (Resource c : classes) {
            Resource instance = m.createResource(c);
            instance.addLiteral(RDFS.label, "a " + c.getProperty(RDFS.label).getLiteral());
            res.add(instance);
        }
        return res;
    }


    public Resource createPhoto(long photoId, long albumId, long ownerId) {
        // create an empty RDF graph
        Model m = ModelFactory.createDefaultModel();
        // create an instance of Photo in Model m
        Resource photoResource = m.createResource(
                Namespaces.getPhotoUri(photoId),
                SempicOnto.Photo
                );

        photoResource.addLiteral(SempicOnto.albumId, albumId);
        photoResource.addLiteral(SempicOnto.ownerId, ownerId);

        return photoResource;
    }

    /**
     * Query a Photo and retrieve all the direct properties of the photo and if
     * the property are depic, takenIn or takenBy, it also retrieve the labels
     * of the object of these properties
     *
     * @param id
     * @return
     */
    public Resource readPhoto(long id, boolean shouldPrint) {
        String pUri = Namespaces.getPhotoUri(id);

        // SPARQL syntax
        // —————————————
        // String s = "CONSTRUCT {"
        //         + "<" + pUri + "> ?p ?o . "
        //         + "?o <" + RDFS.label + "> ?o2 . "
        //         + "?o <" + RDF.type + "> ?o3 . "
        //         + "} "
        //         + "WHERE { "
        //         + "<" + pUri + "> ?p ?o . "
        //         + "OPTIONAL {"
        //         + "?o <" + RDFS.label + "> ?o2 ."
        //         + "?o <" + RDF.type + "> ?o3 ."
        //         + "FILTER (?p IN (<" + SempicOnto.depicts + "> /*,<" + SempicOnto.takenIn + ">,<" + SempicOnto.takenBy + "> */)) "
        //         + "}"
        //         + "}";
        // Model m = this.cnxQueryConstruct(s);

        // Java API
        // ———————
        // ———————

        Triple uriTriple = Triple.create(
                    NodeFactory.createURI(pUri),
                    Var.alloc("p"),
                    Var.alloc("o"));

        Triple labelTriple = Triple.create(
                    Var.alloc("o"),
                    RDFS.label.asNode(),
                    Var.alloc("o2"));

        Triple rdfTriple = Triple.create(
                    Var.alloc("o"),
                    RDF.type.asNode(),
                    Var.alloc("o3"));

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(uriTriple);
        basicPattern.add(labelTriple);
        basicPattern.add(rdfTriple);

        Expr expr = new E_OneOf(
                new ExprVar("p"),
                new ExprList(  new NodeValueNode(SempicOnto.depicts.asNode()) )
                );

        // Java API 1): syntax
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
            elementTriplesBlock.addTriple(uriTriple);
                                                // BasicPattern or QuadAcc
            querySyntaxBuild.setConstructTemplate(new Template(basicPattern));

            // set WHERE clause
            ElementGroup queryPattern = new ElementGroup();
            queryPattern.addTriplePattern(uriTriple);
            queryPattern.addTriplePattern(labelTriple);
            queryPattern.addTriplePattern(rdfTriple);
            ElementFilter elementFilter = new ElementFilter(expr);
            queryPattern.addElementFilter(elementFilter);
            querySyntaxBuild.setQueryPattern(queryPattern);

            System.out.println("querySyntaxBuild " + querySyntaxBuild);
        }

        // Java API 2) Algebra
        // —————————————————————

        Op op;
        // Make a BGP (Basic Graph Pattern) from this pattern
        // see also
        // https://afia.asso.fr/wp-content/uploads/2018/01/Corby_PDIA2017_RechercheSemantique.pdf
        // to understand what is a BGP.
        op = new OpBGP(basicPattern);
        // Filter that pattern with our expression
        op = OpFilter.filter(expr, op);

        // Notice that the query form (SELECT, CONSTRUCT, DESCRIBE, ASK) isn't
        // part of the algebra, and we have to set this in the query (although
        // SELECT is the default). FROM and FROM NAMED are similarly absent.
        // https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html
        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryConstructType();
        queryAlgebraBuild.setConstructTemplate(new Template(basicPattern));
        if (shouldPrint) {
            System.out.println("queryAlgebraBuild " + queryAlgebraBuild);
        }

        // Execution
        // —————————
        // Here we execute only queryAlgebraBuild and not querySyntaxBuild

        Model m = this.cnxQueryConstruct(queryAlgebraBuild);
        return m.getResource(pUri);
    }

}
