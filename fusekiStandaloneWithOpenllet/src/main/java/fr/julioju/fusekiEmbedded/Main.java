package fr.julioju.fusekiEmbedded;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileSystems;

import org.apache.jena.fuseki.main.FusekiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log =
        LoggerFactory.getLogger(Main.class);

    private static FusekiServer fusekiServer;
    private static final int port = 3030;

    private static void serverStartEmbeddedFuseki() {
        Main.fusekiServer = FusekiServer.create()
            .port(port)
            // .loopback(true)
            .parseConfigFile(
                    // Terminated by a dot
                    FileSystems.getDefault().getPath(".").toAbsolutePath().getParent().toString()
                    // Therefore before the first slash there is two dot
                    + "/run/configuration/sempic.ttl"
                    )
            // To enable logging, change value to true
            // I don't know if he does something
            .build();
        log.debug("Server Fuseki succesfully instanciated."
                + " Its port is " + Main.port);
        Main.fusekiServer.start();
    }

    public static void main (String args[]){
        log.info("Start of main");

        try (ServerSocket ss = new ServerSocket(port)) {
        } catch (IOException e) {
            log.error("FATAL: Port " + port + " already in use.");
                System.exit(31);
        }
        Main.serverStartEmbeddedFuseki();

    }
}
