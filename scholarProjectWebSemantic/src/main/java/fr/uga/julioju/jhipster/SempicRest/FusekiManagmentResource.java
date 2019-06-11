package fr.uga.julioju.jhipster.SempicRest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.uga.julioju.sempic.FusekiServerConn;

/**
 * REST controller for stop / start / restart Fuseki
 */
@RestController
@RequestMapping("/api")
public class FusekiManagmentResource  {

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
