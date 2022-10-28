package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall;

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
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.statistikk.oppgavebeholdning.NyeOgFerdigstilteOppgaver;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/saksbehandler/nøkkeltall")
@ApplicationScoped
@Transactional
public class SaksbehandlerNøkkeltallRestTjeneste {

    private KøStatistikkTjeneste køStatistikk;

    public SaksbehandlerNøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public SaksbehandlerNøkkeltallRestTjeneste(KøStatistikkTjeneste køStatistikk) {
        this.køStatistikk = køStatistikk;
    }

    @GET
    @Path("/nye-og-ferdigstilte-oppgaver")
    @Produces("application/json")
    @Operation(description = "Henter en oversikt over hvor mange oppgaver som er opprettet og ferdigstilt de siste syv dagene", tags = "Nøkkeltall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<NyeOgFerdigstilteOppgaver> getNyeOgFerdigstilteOppgaver(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return køStatistikk.hentStatistikk(sakslisteId.getVerdi());
    }
}
