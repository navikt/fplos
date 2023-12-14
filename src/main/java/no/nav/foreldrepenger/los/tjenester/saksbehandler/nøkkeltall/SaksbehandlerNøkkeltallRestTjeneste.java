package no.nav.foreldrepenger.los.tjenester.saksbehandler.nøkkeltall;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NyeOgFerdigstilteOppgaver;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import java.util.List;

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
    public List<NyeOgFerdigstilteOppgaver> getNyeOgFerdigstilteOppgaver(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return køStatistikk.hentStatistikk(sakslisteId.getVerdi());
    }
}
