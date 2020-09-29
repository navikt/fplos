package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.Driftsmelding;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.util.env.Cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

@Path("/driftsmeldinger")
@ApplicationScoped
public class DriftsmeldingerRestTjeneste {

    public DriftsmeldingerRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.APPLIKASJON, sporingslogg = false)
    public List<Driftsmelding> hentDriftsmeldinger() {
        if (Cluster.current() == Cluster.PROD_FSS) {
            return Collections.emptyList();
        }
        return List.of(Driftsmelding.DriftsmeldingBuilder.aDriftsmelding()
                .opprettet(LocalDateTime.now())
                .erAktiv(true)
                .melding("Detten er en dummy driftsmelding. På grunn av solslyng går dessverre ikke toget til Kongsberg som normalt kl 16.00.")
                .build());
    }
}

