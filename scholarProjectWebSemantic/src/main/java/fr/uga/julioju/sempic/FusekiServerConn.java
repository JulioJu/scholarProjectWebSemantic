package fr.uga.julioju.sempic;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

import org.apache.jena.fuseki.main.FusekiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FusekiServerConn  {

    private static final Logger log =
        LoggerFactory.getLogger(FusekiServerConn.class);

    private static FusekiServer fusekiServer;

    private static boolean isEmbeddedFuseki = true;

    // private DatasetGraph datasetGraph;

    // private Dataset dataset;

    private static final int port = 3030;

    public static void serverStart(boolean isEmbeddedFuseki) {
        if (!isEmbeddedFuseki) {
            FusekiServerConn.isEmbeddedFuseki = false;
            return;
        }

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
        FusekiServerConn.fusekiServer = FusekiServer.create()
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
                + " Its port is " + FusekiServerConn.port);
        FusekiServerConn.fusekiServer.start();
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

    private static void serverStop() {
        log.debug("Server Fuseki succesfully destroyed."
                + " Its port " + FusekiServerConn.port + " is released.");
        fusekiServer.stop();
    }

    public static void serverRestart() throws InterruptedException {
        if (!FusekiServerConn.isEmbeddedFuseki) {
            return;
        }
        FusekiServerConn.serverStop();
        TimeUnit.SECONDS.sleep(10);
        FusekiServerConn.serverStart(FusekiServerConn.isEmbeddedFuseki);
    }

}
