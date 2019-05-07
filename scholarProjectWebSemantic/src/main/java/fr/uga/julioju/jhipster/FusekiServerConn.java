package fr.uga.julioju.jhipster;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

import org.apache.jena.fuseki.main.FusekiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class FusekiServerConn  {

    private final Logger log = LoggerFactory.getLogger(FusekiServerConn.class);

    private FusekiServer fusekiServer;

    // private DatasetGraph datasetGraph;

    // private Dataset dataset;

    private final int port = 3030;

    public void serverStart() {

        // https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
        try (ServerSocket ss = new ServerSocket(port)) {
        } catch (IOException e) {
            log.error("FATAL: Port " + port + " already in used. " +
                    "Note: can't use hot swapping with Spring devtools" +
                    " See my solutions in README.md. ");
            System.exit(30);
        }

        // To enable complete logging, uncomment `.verbose(true)` line above too
        // FusekiLogging.setLogging();
        this.fusekiServer = FusekiServer.create()
            .port(port)
            .loopback(true)
            .parseConfigFile(
                    // Terminated by a dot
                    // TODO change location to not be in scholarProjectWebSemanticFusekiDatabase
                    FileSystems.getDefault().getPath(".").toAbsolutePath().getParent().getParent().toString()
                    // Therefore before the first slash there is two dot
                    + "/scholarProjectWebSemanticFusekiDatabase/run/configuration/sempic.ttl"
                    )
            // To enable logging, uncomment also FusekiLogging.setLogging() line
            // above
            // .verbose(true)
            .build();
        log.debug("Server Fuseki succesfully instanciated."
                + " Its port is " + this.port);
        this.fusekiServer.start();
    }

    // private void killThreadOnPort3030() {
    //     try (ServerSocket ss = new ServerSocket(port)) {
    //     } catch (IOException e) {
    //         log.error("Port " + port + " already in used. "
    //                 + " First interrupt thread on Port 3030, then instantiate Fuseki.");
    //
    //         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    //         threadSet.forEach(t -> {
    //             if (t.getName().contains("localhost:" + port)) {
    //                 System.out.println("Contains");
    //                 t.interrupt();
    //                 try {
    //                     TimeUnit.SECONDS.sleep(10);
    //                 } catch (InterruptedException e1) {
    //                     e1.printStackTrace();
    //                 }
    //             }
    //         });
    //         log.debug("Now, list of current threads:");
    //         Thread.getAllStackTraces().keySet().forEach(System.out::println);
    //     }
    // }

    private void serverStop() {
        log.debug("Server Fuseki succesfully destroyed."
                + " Its port " + this.port + " is released.");
        fusekiServer.stop();
    }

    public void serverRestart() throws InterruptedException {
        this.serverStop();
        TimeUnit.SECONDS.sleep(10);
        this.serverStart();
    }

    // public Dataset getDataset() {
    //     return dataset;
    // }

}
