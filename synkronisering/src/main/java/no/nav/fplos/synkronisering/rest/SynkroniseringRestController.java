package no.nav.fplos.uuid.rest;

import no.nav.fplos.synkronisering.SynkroniseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class SynkroniseringRestController {
    private SynkroniseringService uuidSyncService;

    @Autowired
    public SynkroniseringRestController(SynkroniseringService uuidSyncService) {
        this.uuidSyncService = uuidSyncService;
    }

    @GetMapping(path = "/oppdater", produces = "application/json")
    public ResponseEntity oppdater() {
        uuidSyncService.oppdater();
        return ResponseEntity.ok().build();
    }

}
