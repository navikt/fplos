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
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.NyeOgFerdigstilteOppgaver;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("/saksbehandler/nokkeltall")
@ApplicationScoped
@Transactional
public class SaksbehandlerNøkkeltallRestTjeneste {

    private OppgaveStatistikk oppgaveStatistikk;

    public SaksbehandlerNøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public SaksbehandlerNøkkeltallRestTjeneste(OppgaveStatistikk oppgaveStatistikk) {
        this.oppgaveStatistikk = oppgaveStatistikk;
    }

    @GET
    @Path("/nye-og-ferdigstilte-oppgaver")
    @Produces("application/json")
    @Operation(description = "Henter en oversikt over hvor mange oppgaver som er opprettet og ferdigstilt de siste syv dagene", tags = "Nøkkeltall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<NyeOgFerdigstilteOppgaver> getNyeOgFerdigstilteOppgaver(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveStatistikk.hentStatistikk(sakslisteId.getVerdi());
    }
}
