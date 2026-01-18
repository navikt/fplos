package no.nav.foreldrepenger.los.tjenester.admin;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.tjenester.admin.driftsmelding.Driftsmelding;
import no.nav.foreldrepenger.los.tjenester.admin.driftsmelding.DriftsmeldingTjeneste;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftsmeldingDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/driftsmeldinger")
@Transactional
@ApplicationScoped
public class DriftsmeldingerRestTjeneste {
    private DriftsmeldingTjeneste driftsmeldingTjeneste;

    @Inject
    public DriftsmeldingerRestTjeneste(DriftsmeldingTjeneste driftsmeldingTjeneste) {
        this.driftsmeldingTjeneste = driftsmeldingTjeneste;
    }

    public DriftsmeldingerRestTjeneste() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Driftsmeldinger", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.APPLIKASJON, sporingslogg = false)
    public List<DriftsmeldingDto> hentAktiveDriftsmeldinger() {
        return driftsmeldingTjeneste.hentAktiveDriftsmeldinger().stream().map(DriftsmeldingerRestTjeneste::tilDto).toList();
    }

    private static DriftsmeldingDto tilDto(Driftsmelding driftsmelding) {
        return new DriftsmeldingDto(String.valueOf(driftsmelding.getId()), driftsmelding.getMelding(), driftsmelding.getAktivFra(),
            driftsmelding.getAktivTil());
    }

}

