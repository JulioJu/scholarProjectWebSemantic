package fr.uga.julioju.jhipster;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import fr.uga.julioju.jhipster.config.ApplicationProperties;
import fr.uga.julioju.jhipster.config.DefaultProfileUtil;
import fr.uga.julioju.sempic.FusekiServerConn;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class ScholarProjectWebSemanticApp implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ScholarProjectWebSemanticApp.class);

    /**
     * Initializes scholarProjectWebSemantic.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ScholarProjectWebSemanticApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    // Added by JulioJu
    // ————————————————
        if (args.length == 0) {
            ScholarProjectWebSemanticApp.mvnArgumentError();
        }
        switch (args[0]) {
            case "fusekiServerEmbedded":
                log.info("fusekiServerEmbedded");
                FusekiServerConn.fusekiServerNotManaged = false;
                FusekiServerConn.isEmbeddedFuseki = true;
            break;
            case "fusekiServerNoEmbedded":
                log.info("fusekiServerNotEmbedded");
                FusekiServerConn.fusekiServerNotManaged = false;
                FusekiServerConn.isEmbeddedFuseki = false;
            break;
            case "fusekiServerNotManaged":
                log.info("Fuseki should be start outside the app.");
                FusekiServerConn.fusekiServerNotManaged = true;
            break;
            default:
                ScholarProjectWebSemanticApp.mvnArgumentError();
            break;

        }
        if (!FusekiServerConn.fusekiServerNotManaged) {
            FusekiServerConn.serverStart();
        }
    }

    private static void mvnArgumentError() {
        log.error("FATAL: The first argument should not be null.\n"
                + "Call `$ mvn' either like:\n"
                + "\t`$ mvn -Dspring-boot.run.arguments=\"fusekiServerEmbedded (deprecated)\"\n'"
                + "\t`$ mvn -Dspring-boot.run.arguments=\"fusekiServerNoEmbedded\"\n'"
                + "\t`$ mvn -Dspring-boot.run.arguments=\"fusekiServerNotManaged\"\n'"
                );
        System.exit(29);
    }
    // End of added by JulioJu

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }
}
