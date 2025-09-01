package no.nav.foreldrepenger.los.tjenester.saksbehandler.nøkkeltall;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/saksbehandler/nøkkeltall")
@ApplicationScoped
@Transactional
public class DummyNøkkeltallRestTjeneste {

    public DummyNøkkeltallRestTjeneste() {
        // CDI
    }

    @GET
    @Path("/nye-og-ferdigstilte-oppgaver")
    @Produces("application/json")
    @Operation(description = "Dummy nøkkeltall-tjeneste", tags = "Nøkkeltall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public Response getNyeOgFerdigstilteOppgaver(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        // Beholder dummy variant i tilfelle gammel klientkode
        // Kommer ny PR som fjerner endepunkt + STATISTIKK_KO-tabellen
        return Response.ok(List.of()).build();
    }
}
