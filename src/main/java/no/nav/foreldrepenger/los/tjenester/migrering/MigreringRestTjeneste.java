package no.nav.foreldrepenger.los.tjenester.migrering;

import java.util.function.Function;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.BehandlingTjeneste;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/migrering")
@ApplicationScoped
@Transactional
public class MigreringRestTjeneste {

    private BehandlingTjeneste behandlingTjeneste;

    @Inject
    public MigreringRestTjeneste(BehandlingTjeneste behandlingTjeneste) {
        this.behandlingTjeneste = behandlingTjeneste;

    }

    public MigreringRestTjeneste() {
        // For Rest-CDI
    }

    @POST
    @Path("/lagrebehandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "lagrer ned behandlingdto som behandling, rører ikke oppgave", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public Response synkroniserBehandling(@TilpassetAbacAttributt(supplierClass = LosBehandlingDtoAbacDataSupplier.class)
        @NotNull @Valid LosBehandlingDto dto) {
        var fagsystem = Kildesystem.FPSAK.equals(dto.kildesystem()) ? Fagsystem.FPSAK : Fagsystem.FPTILBAKE;
        behandlingTjeneste.lagreBehandling(dto, fagsystem);
        return Response.ok().build();
    }

    public static class LosBehandlingDtoAbacDataSupplier implements Function<Object, AbacDataAttributter> {

        @Override
        public AbacDataAttributter apply(Object obj) {
            return AbacDataAttributter.opprett();
        }
    }


}
