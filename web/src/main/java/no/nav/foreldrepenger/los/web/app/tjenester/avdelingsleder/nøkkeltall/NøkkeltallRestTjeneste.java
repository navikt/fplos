package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall;

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
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaveBeholdningStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForAvdelingPerDato;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.OppgaverForAvdelingSattManueltPåVent;
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
    @Path("/behandlinger-manuelt-vent-historikk")
    @Produces("application/json")
    @Operation(description = "ManueltVent Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public List<OppgaverForAvdelingSattManueltPåVent> getAntallOppgaverSattPåManuellVentForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return oppgaveBeholdningStatistikkTjeneste.hentAntallOppgaverForAvdelingSattManueltPåVent(avdelingEnhet.getAvdelingEnhet());
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
