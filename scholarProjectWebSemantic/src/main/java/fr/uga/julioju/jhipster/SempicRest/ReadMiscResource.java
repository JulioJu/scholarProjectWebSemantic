package fr.uga.julioju.jhipster.SempicRest;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.ReadMisc;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;

/**
 * REST controller for retrieve misc things in Database
 */
@RestController
@RequestMapping("/api")
public class ReadMiscResource  {

    private final Logger log = LoggerFactory.getLogger(ReadMiscResource.class);

    /**
     * GET  //rdfquery_listsubclassof/:classQuery : get subclasses of classQuery
     *      (ontology SempicOnto)
     *
     * @return the response with status 200 (OK) and the result of rdfquery in body
     *  or response with status 404 if ":classQuery" doesn't exist
     */
    @GetMapping("/rdfquery_listsubclassof/{classQuery}")
    public ResponseEntity<List<String>> getRdfQuery(
            @PathVariable String classQuery) {

        log.debug("REST request to get subclass of:", classQuery);

        ArrayList<String> results = new ArrayList<String>();

        List<Resource> classes = ReadMisc.listSubClassesOf(
                    (Node_URI) NodeFactory.createURI(SempicOnto.NS + classQuery));
        classes.forEach(c -> { results.add(c.toString()); });
        log.debug("Subclasses: ", classes.toString());

        return ResponseEntity.ok().body(results);
    }

}
