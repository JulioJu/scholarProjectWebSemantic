package fr.uga.julioju.jhipster.SempicRest;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fr.uga.julioju.sempic.RDFStore;
import fr.uga.julioju.sempic.ResponseQuery;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

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

}
