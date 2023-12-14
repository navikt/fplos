package no.nav.foreldrepenger.los.tjenester.saksbehandler.saksliste;

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

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/saksbehandler/saksliste")
@ApplicationScoped
@Transactional
public class SaksbehandlerSakslisteRestTjeneste {

    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public SaksbehandlerSakslisteRestTjeneste(OppgaveKøTjeneste oppgaveKøTjeneste, SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public SaksbehandlerSakslisteRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter sakslister", tags = "Saksliste")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public List<SakslisteDto> hentSakslister() {
        var filtre = oppgaveKøTjeneste.hentOppgaveFiltreringerForPåloggetBruker();
        return filtre.stream().map(o -> new SakslisteDto(o, oppgaveKøTjeneste.hentAntallOppgaver(o.getId(), false))).toList();
    }

    @GET
    @Path("/saksbehandlere")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter saksbehandlere tilknyttet en saksliste", tags = "Saksliste")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public List<SaksbehandlerDto> hentSakslistensAktiveSaksbehandlere(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return saksbehandlerDtoTjeneste.hentAktiveSaksbehandlereTilknyttetSaksliste(sakslisteId.getVerdi());
    }
}
