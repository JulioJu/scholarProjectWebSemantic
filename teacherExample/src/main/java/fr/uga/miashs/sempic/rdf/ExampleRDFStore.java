/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();

        Resource pRes = s.createPhoto(1, 1, 1);

        Model m = ModelFactory.createDefaultModel();
        Resource newAnimal = m.createResource(SempicOnto.Animal);
        newAnimal.addLiteral(RDFS.label, "Medor");
        m.add(pRes, SempicOnto.depicts, newAnimal);
        m.write(System.out, "turtle");

        s.saveModel(m);
        
        //s.deleteModel(m);
        //s.cnx.load(m);
        List<Resource> classes = s.listSubClassesOf(SempicOnto.Depiction);
        classes.forEach(c -> {System.out.println(c);});

        List<Resource> instances = s.createAnonInstances(classes);
        instances.forEach(i -> {
            System.out.println(i.getProperty(RDFS.label));
        });

        //s.deleteModel(m);
        //s.readPhoto(1).getModel().write(System.out,"turtle");
        // print the graph on the standard output
        //pRes.getModel().write(System.out);
    }
}
