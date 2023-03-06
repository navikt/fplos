package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/avdelingsleder/nøkkeltall")
@ApplicationScoped
@Transactional
public class NøkkeltallÅpneBehandlingerRestTjeneste {

    private NøkkeltallBehandlingerVentestatus nøkkeltallBehandlingerVentestatus;

    public NøkkeltallÅpneBehandlingerRestTjeneste() {
    }

    @Inject
    public NøkkeltallÅpneBehandlingerRestTjeneste(NøkkeltallBehandlingerVentestatus statistikkTjeneste) {
        this.nøkkeltallBehandlingerVentestatus = statistikkTjeneste;
    }

    @GET
    @Path("/åpne-behandlinger")
    @Produces("application/json")
    @Operation(description = "Åpne behandlinger", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<NøkkeltallBehandlingVentestatusDto> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallBehandlingerVentestatus.hentBehandlingVentestatusNøkkeltall(avdelingEnhet.getAvdelingEnhet());
    }

}
