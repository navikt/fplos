package no.nav.fplos.uuid.rest;

import no.nav.fplos.uuid.UUIDSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class UUIDSyncRestController {
    private UUIDSyncService uuidSyncService;

    @Autowired
    public UUIDSyncRestController(UUIDSyncService uuidSyncService) {
        this.uuidSyncService = uuidSyncService;
    }

    @GetMapping(path="/oppdater", produces = "application/json")
    public ResponseEntity oppdaterUUID(){
        uuidSyncService.oppdaterUUID();
        return ResponseEntity.ok().build();
    }

}
