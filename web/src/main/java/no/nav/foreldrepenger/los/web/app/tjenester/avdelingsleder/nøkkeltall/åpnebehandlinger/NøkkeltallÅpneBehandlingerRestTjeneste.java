package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentefristUtløperDto;
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
    public List<NøkkeltallBehandlingFørsteUttakDto> getAlleBehandlingerForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallBehandlingerVentestatus.hentBehandlingVentestatusNøkkeltall(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/frist-utløp")
    @Produces("application/json")
    @Operation(description = "Ventetilstand frister pr uke", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<NøkkeltallBehandlingVentefristUtløperDto> getAlleVentefristerForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallBehandlingerVentestatus.hentVentefristNøkkeltall(avdelingEnhet.getAvdelingEnhet());
    }

}
