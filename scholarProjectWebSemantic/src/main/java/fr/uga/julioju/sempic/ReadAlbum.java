package fr.uga.julioju.sempic;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;

public class ReadAlbum  {

    private static final Logger log =
        LoggerFactory.getLogger(ReadAlbum.class);

    public static boolean isAdmin(Model model) {
        if (
                model.listObjectsOfProperty(RDF.type)
                .toList().contains(SempicOnto.AdminGroup)
        ) {
            return true;
        }
        return false;
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
    public static Model read(long id) {
        String uri = Namespaces.getAlbumUri(id);

        Triple tripleUri = Triple.create(
                    NodeFactory.createURI(uri),
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

        // Java API 2) Algebra form of the query
        // —————————————————————

        Op opLeft = new OpBGP(basicPatternWhere);
        Op opRight = new OpBGP(basicPatterWhereOptional);

        Op op = OpLeftJoin.createLeftJoin(opLeft, opRight, null);

        Query queryAlgebraBuild = OpAsQuery.asQuery(op);
        queryAlgebraBuild.setQueryConstructType();

        queryAlgebraBuild.setConstructTemplate(new Template(basicPattern));

        log.debug("queryAlgebraBuild\n" + queryAlgebraBuild);

        // Execution
        // —————————

        Model m = RDFConn.cnxQueryConstruct(queryAlgebraBuild);
        return m;
    }

}
