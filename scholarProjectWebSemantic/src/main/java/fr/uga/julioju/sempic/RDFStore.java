package fr.uga.julioju.sempic;

import fr.uga.julioju.sempic.Exceptions.FusekiDownException;
import fr.uga.julioju.sempic.Exceptions.FusekiJenaQueryException;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.update.Update;
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

    private void cnxUpdate (Update u) {
        try {
            cnx.update(u);
        } catch (HttpException e) {
            throw new FusekiDownException();
        }
    }

    private void cnxUpdate (String u) {
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
     * Delete the given model from the triple store. Be carreful: Blank nodes
     * are replaced by variables
     * @param m the model to be deleted
     */
    public void deleteModel(Model m) {
        HashMap<Resource, Var> map = new HashMap<>();
        QuadAcc acc = new QuadAcc();
        m.listStatements().forEachRemaining(st -> {
            if (st.getSubject().isAnon() || st.getObject().isAnon()) {
                Node s = blankNodeAsVariable(st.getSubject(), map);
                Node p = st.getPredicate().asNode();
                Node o = blankNodeAsVariable(st.getObject(), map);

                acc.addTriple(new Triple(s, p, o));
            } else {

                acc.addTriple(st.asTriple());
            }
        });

        Update u = new UpdateDeleteWhere(acc);
        //System.out.println(u);
        cnx.begin(ReadWrite.WRITE);
        this.cnxUpdate(u);
        this.cnxCommit();
    }

    private Node blankNodeAsVariable(Resource r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            Var v = bnMap.get(r);
            if (v == null) {
                bnMap.put(r, v = Var.alloc("A" + bnMap.size()));
           }
            return v;
        }
        return r.asNode();
    }

    private Node blankNodeAsVariable(RDFNode r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            return blankNodeAsVariable(r.asResource(), bnMap);
        }
        return r.asNode();
    }

    /**
     * Delete all the statements where the resource appears as subject or object
     * @param r The named resource to be deleted (the resource cannot be annonymous)
     */
    public void deleteResource(Resource r) {
        if (r.isURIResource()) {
            cnx.begin(ReadWrite.WRITE);
            this.cnxUpdate("DELETE WHERE { <" + r.getURI() + "> ?p ?o }");
            this.cnxUpdate("DELETE WHERE { ?s ?p <" + r.getURI() + "> }");
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
    public Resource readPhoto(long id) {
        String pUri = Namespaces.getPhotoUri(id);

        String s = "CONSTRUCT {"
                + "<" + pUri + "> ?p ?o . "
                + "<" + pUri + "> ?p1 ?o1 . "
                + "?o1 <" + RDFS.label + "> ?o2 . "
                + "?o1 <" + RDF.type + "> ?o4 . "
                + "} "
                + "WHERE { "
                + "<" + pUri + "> ?p ?o . "
                + "OPTIONAL {"
                + "<" + pUri + "> ?p1 ?o1 ."
                + "?o1 <" + RDFS.label + "> ?o2 ."
                + "?o1 <" + RDF.type + "> ?o4 ."
                + "FILTER (?p1 IN (<" + SempicOnto.depicts + ">,<" + SempicOnto.takenIn + ">,<" + SempicOnto.takenBy + ">)) "
                + "}"
                + "}";
        Model m = this.cnxQueryConstruct(s);
        return m.getResource(pUri);
    }


}
