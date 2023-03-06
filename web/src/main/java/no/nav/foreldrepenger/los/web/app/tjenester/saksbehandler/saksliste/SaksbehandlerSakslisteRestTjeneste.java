package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste;

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
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
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
    public SaksbehandlerSakslisteRestTjeneste(OppgaveKøTjeneste oppgaveKøTjeneste,
                                              SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
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
        return filtre.stream()
                .map(o -> new SakslisteDto(o, oppgaveKøTjeneste.hentAntallOppgaver(o.getId(), false)))
                .toList();
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
