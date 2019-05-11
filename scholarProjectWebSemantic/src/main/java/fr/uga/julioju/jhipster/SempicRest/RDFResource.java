package fr.uga.julioju.jhipster.SempicRest;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.FusekiServerConn;
import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ResponseQuery;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

/**
 * REST controller for managing RDFResource.
 */
@RestController
@RequestMapping("/api")
public class RDFResource  {

    private final Logger log = LoggerFactory.getLogger(RDFResource.class);

    /**
     * GET  //rdfquery_listsubclassof/:classQuery : get subclasses of classQuery
     *      (ontology SempicOnto)
     *
     * @return the response with status 200 (OK) and the result of rdfquery in body
     *  or response with status 404 if ":classQuery" doesn't exist
     */
    @GetMapping("/rdfquery_listsubclassof/{classQuery}")
    public ResponseEntity<ResponseQuery>
    getRdfQuery(@PathVariable String classQuery) {

        log.debug("REST request to get subclass of:", classQuery);

        ArrayList<String> results = new ArrayList<String>();

        List<Resource> classes =
            RDFStore.listSubClassesOf(SempicOnto.NS + classQuery);
        classes.forEach(c -> { results.add(c.toString()); });
        log.debug("Subclasses: ", classes.toString());

        return ResponseEntity.ok()
            .body(new ResponseQuery(results));
    }

    /**
     * GET  /startFusekiProcess : start Fuseki Server
     *
     * @return status 200 (OK). If the start fail, server is existed with
     *      an exist code > 0
     */
    @GetMapping("/startFusekiProcess")
    public ResponseEntity<Void> startFuseki() {
        FusekiServerConn.serverStart();
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /stopFusekiProcess : stop Fuseki Server
     *
     * @return status 200 (OK). If the stop fail, server is existed with
     *      an exist code > 0
     */
    @GetMapping("/stopFusekiProcess")
    public ResponseEntity<Void> stopFuseki() {
        FusekiServerConn.serverStop();
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /restartFusekiProcess : restart Fuseki Server
     *
     * @return status 200 (OK). If the restart fail, server is existed with
     *      an exist code > 0
     */
    @GetMapping("/restartFusekiProcess")
    public ResponseEntity<Void> restartFuseki() {
        FusekiServerConn.serverRestart();
        return ResponseEntity.ok().build();
    }

}
