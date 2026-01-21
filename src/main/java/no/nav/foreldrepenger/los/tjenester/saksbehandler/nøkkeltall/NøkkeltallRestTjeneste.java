package no.nav.foreldrepenger.los.tjenester.saksbehandler.nøkkeltall;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.statistikk.AktiveOgTilgjenglige;
import no.nav.foreldrepenger.los.statistikk.StatistikkRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/saksbehandler/nøkkeltall")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Transactional
public class NøkkeltallRestTjeneste {

    private static final String ENHET_QUERY_NAME = "oppgaveFilterId";
    private static final int CUT_OFF = 25;

    private KøStatistikkTjeneste køStatistikkTjeneste;
    private StatistikkRepository statistikkRepository;

    public NøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public NøkkeltallRestTjeneste(KøStatistikkTjeneste køStatistikkTjeneste, StatistikkRepository statistikkRepository) {
        this.køStatistikkTjeneste = køStatistikkTjeneste;
        this.statistikkRepository = statistikkRepository;
    }

    @GET
    @Path("/oppgaver")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public List<AktiveOgTilgjenglige> aktiveOgTilgjengligeOppgaverStatistikkForKø(@QueryParam(ENHET_QUERY_NAME) @NotNull @Valid Long oppgaveFilterId) {
        return statistikkRepository.hentStatistikkOppgaveFilterFraFom(oppgaveFilterId, LocalDate.now().minusMonths(1)).stream()
            .map(no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste::tilAktiveOgTilgjenglige)
            .sorted(Comparator.comparing(AktiveOgTilgjenglige::tidspunkt))
            .toList();
    }

    @GET
    @Path("/oppgaver/siste")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public int antallOppgaverForKø(@QueryParam(ENHET_QUERY_NAME) @NotNull @Valid Long oppgaveFilterId) {
        var statistikkOppgaveFilter = statistikkRepository.hentSisteStatistikkOppgaveFilter(oppgaveFilterId);
        if (statistikkOppgaveFilter.getAntallAktive() > CUT_OFF) {
            return statistikkOppgaveFilter.getAntallAktive();
        }
        // Henter ferske tall pga lav andel oppgaver
        return køStatistikkTjeneste.hentAntallOppgaver(oppgaveFilterId);
    }


}
