package fr.uga.julioju.sempic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static boolean isEmbeddedFuseki = true;

    private static Process fusekiProcess;

    private static Thread threadPrintFuseki;

    // private DatasetGraph datasetGraph;

    // private Dataset dataset;

    private static final int port = 3030;

    private static void serverStartEmbeddedFuseki() {

        // FusekiServerConn.killThreadOnPort3030();

        // Done nothing
        // FusekiLogging.setLogging(FileSystems.getDefault().getPath("/tmp/fusekiLogs.log"));

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
            // To enable logging, change value to true
            // I don't know if he does something
            .verbose(false)
            .build();
        log.debug("Server Fuseki succesfully instanciated."
                + " Its port is " + FusekiServerConn.port);
        FusekiServerConn.fusekiServer.start();
    }

    public static void serverStart() {
        // https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
        try (ServerSocket ss = new ServerSocket(port)) {
        } catch (IOException e) {
            if (FusekiServerConn.isEmbeddedFuseki) {
                log.error("FATAL: Port " + port + " already in use.");
                    System.exit(31);
            } else {

                // Thread.getAllStackTraces().keySet().forEach(System.out::println);

                log.warn("Port " + port + " already in use."
                        + "Trying to release it thanks ../FusekiStop.sh. ");
                try {
                    Process process =
                        Runtime.getRuntime().exec("bash ../FusekiStop.sh");
                    process.waitFor();
                    if (process.exitValue() != 0) {

                        log.error("FATAL: Port " + port + " already in use"
                                + "by an other process than a "
                                + "standalone Fuseki");
                        System.exit(32);
                    } else {
                        log.info("Port " + port + " released.");
                    }
                } catch (IOException | InterruptedException e1) {
                    log.error("FATAL:\n" + e1.toString());
                    System.exit(33);
                }
            }

        }

        if (isEmbeddedFuseki) {
            FusekiServerConn.serverStartEmbeddedFuseki();
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {

                FusekiServerConn.fusekiProcess = runtime.exec(
                    "bash ../FusekiStart.sh");
                FusekiServerConn.threadPrintFuseki = new Thread (() -> {
                    try {
                        // (see https://stackoverflow.com/questions/792024/how-to-execute-system-commands-linux-bsd-using-java)
                        BufferedReader b = new BufferedReader(new InputStreamReader(
                                    FusekiServerConn.fusekiProcess.getInputStream()));
                        String line = "";
                        while ((line = b.readLine()) != null) {
                            System.out.println(
                                    "\033[4;34mFUSEKI SERVER MESSAGE:\033[0m"
                                    + line);
                        }
                        b.close();
                    } catch (IOException e) {
                        log.error("FATAL:\n" + e.toString());
                        System.exit(34);
                    }
                });
                threadPrintFuseki.start();
                log.debug("THE THREAD CREATED TO PRINT FUSEKI SERVER TRACE IS: "
                        + threadPrintFuseki.getName()
                        + "To see if still exists, you could use `$ jtrace -l "
                        + ProcessHandle.current().pid()
                        + "'(check only the thread number).");
                // task.run();
            } catch (IOException e) {
                log.error("FATAL:\n" + e.toString());
                System.exit(35);
            }
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
    }

    // private static void killThreadOnPort3030() {
    //     try (ServerSocket ss = new ServerSocket(port)) {
    //     } catch (IOException e) {
    //         log.error("Port " + port + " already in used. "
    //                 + " First interrupt thread on Port 3030, then instantiate Fuseki.");
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

    /**
      * For Standalone Fuseki return true if the server is toped
      * otherwise false.
      * For embedded Fuseki server, can't test if it is already alive,
      * (at least test if port is free) return always true.
      */
    public static boolean serverStop() {
        if (FusekiServerConn.isEmbeddedFuseki) {
            fusekiServer.stop();
        } else {
            // if (FusekiServerConn.FusekiProcess.isAlive()) {
            // If not alive, no action
                threadPrintFuseki.interrupt();
                // We chould destrop the child, not parents, otherwise fail
                // FusekiServerConn.fusekiProcess.destroy();
                FusekiServerConn.fusekiProcess.descendants()
                    .forEach(ProcessHandle::destroy);
            // }
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
        if (!FusekiServerConn.isEmbeddedFuseki) {
            if (FusekiServerConn.fusekiProcess.isAlive()) {
                return false;
            }
        }
        log.debug(" The part " + FusekiServerConn.port
                + " is released.");
        return true;
    }

    public static void serverRestart() {
        FusekiServerConn.serverStop();
        FusekiServerConn.serverStart();
    }

}
