package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall;

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
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaveBeholdningStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForAvdelingPerDato;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForFørsteStønadsdag;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/avdelingsleder/nøkkeltall")
@ApplicationScoped
@Transactional
public class NøkkeltallRestTjeneste {

    private OppgaveBeholdningStatistikkTjeneste oppgaveBeholdningStatistikkTjeneste;

    public NøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public NøkkeltallRestTjeneste(OppgaveBeholdningStatistikkTjeneste oppgaveBeholdningStatistikkTjeneste) {
        this.oppgaveBeholdningStatistikkTjeneste = oppgaveBeholdningStatistikkTjeneste;
    }

    @GET
    @Path("/behandlinger-under-arbeid")
    @Produces("application/json")
    @Operation(description = "UnderArbeid", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<OppgaverForAvdeling> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return oppgaveBeholdningStatistikkTjeneste.hentAlleOppgaverForAvdeling(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-under-arbeid-historikk")
    @Produces("application/json")
    @Operation(description = "UA Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<OppgaverForAvdelingPerDato> getAntallOppgaverForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return oppgaveBeholdningStatistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-første-stønadsdag")
    @Produces("application/json")
    @Operation(description = "Første stønadsdag", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<OppgaverForFørsteStønadsdag> getOppgaverPerFørsteStønadsdag(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return oppgaveBeholdningStatistikkTjeneste.hentOppgaverPerFørsteStønadsdag(avdelingEnhet.getAvdelingEnhet());
    }
}
