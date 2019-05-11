package fr.uga.julioju.sempic;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.update.UpdateRequest;

import fr.uga.julioju.sempic.Exceptions.FusekiDownException;
import fr.uga.julioju.sempic.Exceptions.FusekiJenaQueryException;

public class RDFConn {

    // private final static String ENDPOINT_QUERY = "http://localhost:3030/sempic/sparql"; // SPARQL endpoint
    // private final static String ENDPOINT_UPDATE = "http://localhost:3030/sempic/update"; // SPARQL UPDATE endpoint
    // private final static String ENDPOINT_GSP = "http://localhost:3030/sempic/data"; // Graph Store Protocol

    private static RDFConnection conn() {
        return RDFConnectionFuseki.create()
            .destination("http://localhost:3030/sempic/").build();
    }

    private static void cnxCommit(RDFConnection conn) {
        try {
            conn.commit();
            conn.end();
        } catch (QueryExceptionHTTP e) {
            throw new FusekiDownException(conn, e.toString());
        }
    }

    protected static Model cnxQueryConstruct (Query q) {
        // return Txn.calculateRead(fusekiServerConn.getDataset(), ()-> {
        //     try(QueryExecution qExec =
        //             QueryExecutionFactory.create(q,
        //                 fusekiServerConn.getDataset())) {
        //         // ResultSetFormatter.out(qExec.execAsk());
        //         return qExec.execConstruct();
        //     }
        // }) ;
        try (RDFConnection conn = RDFConn.conn()) {
            conn.begin(ReadWrite.READ);
            try {
                Model model = conn.queryConstruct(q);
                conn.end();
                return model;
            } catch (QueryExceptionHTTP e) {
                throw new FusekiDownException(conn, e.toString());
            } catch (QueryException e) {
                throw new FusekiJenaQueryException(conn, e.toString());
            }
        }
    }

    public static boolean cnxQueryAsk (Query q) {
        // return Txn.calculateRead(fusekiServerConn.getDataset(), ()-> {
        //     try(QueryExecution qExec =
        //             QueryExecutionFactory.create(q,
        //                 fusekiServerConn.getDataset())) {
        //         return qExec.execAsk();
        //     }
        // }) ;
        try (RDFConnection conn = RDFConn.conn()) {
            conn.begin(ReadWrite.READ);
            boolean result;
            try {
                result = conn.queryAsk(q);
                conn.end();
                return result;
            } catch (QueryExceptionHTTP e) {
                throw new FusekiDownException(conn, e.toString());
            } catch (QueryException e) {
                throw new FusekiJenaQueryException(conn, e.toString());
            }
        }
    }

    protected static void cnxUpdateRequest (UpdateRequest u) {
        try (RDFConnection conn = RDFConn.conn()) {
            conn.begin(ReadWrite.WRITE);
            try {
                conn.update(u);
            } catch (QueryExceptionHTTP e) {
                throw new FusekiDownException(conn, e.toString());
            }
            RDFConn.cnxCommit(conn);
        }
    }

    /**
     * Save the given model into the triple store.
     * @param m THe Jena model to be persisted
     */
    public static void saveModel(Model m) {
        try (RDFConnection conn = RDFConn.conn()) {
            conn.begin(ReadWrite.WRITE);
            try {
                conn.load(m);
            } catch (QueryExceptionHTTP e) {
                throw new FusekiDownException(conn, e.toString());
            }
            RDFConn.cnxCommit(conn);
        }
    }

}
