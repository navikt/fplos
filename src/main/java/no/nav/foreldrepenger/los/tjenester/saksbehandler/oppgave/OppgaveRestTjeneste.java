package no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave;

import static no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveDtoTjeneste.ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIderDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.SaknummerIderDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path(OppgaveRestTjeneste.OPPGAVER_BASE_PATH)
@ApplicationScoped
@Transactional
public class OppgaveRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRestTjeneste.class);

    public static final String OPPGAVER_BASE_PATH = "/saksbehandler/oppgaver";
    public static final String OPPGAVER_STATUS_PATH = "/status";
    public static final String OPPGAVER_RESULTAT_PATH = "/resultat";

    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;

    @Inject
    public OppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                               OppgaveKøTjeneste oppgaveKøTjeneste,
                               OppgaveDtoTjeneste oppgaveDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
    }

    public OppgaveRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Init hent oppgaver", tags = "Saksbehandler", responses = {@ApiResponse(responseCode = "202", description = "Hent oppgaver initiert, Returnerer link til å polle etter nye oppgaver", headers = {@Header(name = "Location")})})
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public Response hentOppgaver(@Context HttpServletRequest request,
                                 @NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId,
                                 @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaveIder) throws URISyntaxException {
        return Redirect.sendTilStatus(request, sakslisteId, oppgaveIder);
    }

    @GET
    @Path(OPPGAVER_STATUS_PATH)
    @Operation(description = "Url for å polle på oppgaver asynkront", tags = "Saksbehandler", responses = {@ApiResponse(responseCode = "200", description = "Returnerer Status", content = @Content(schema = @Schema(implementation = AsyncPollingStatus.class))), @ApiResponse(responseCode = "303", description = "Nye oppgaver tilgjenglig", headers = {@Header(name = "Location")})})
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public Response hentNesteOppgaverOgSjekkOmDisseErNye(@Context HttpServletRequest request,
                                                         @NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId,
                                                         @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaverIder) throws URISyntaxException {
        List<Long> oppgaveIderSomVises = oppgaverIder == null ? List.of() : oppgaverIder.getOppgaveIdeer();
        if (oppgaveIderSomVises.isEmpty()) {
            if (!oppgaveDtoTjeneste.finnesTilgjengeligeOppgaver(sakslisteId)) {
                return Redirect.sendTilPolling(request, sakslisteId, oppgaverIder);
            }
            return Redirect.sendTilResultat(request, sakslisteId);
        }
        if (oppgaveTjeneste.erAlleOppgaverFortsattTilgjengelig(oppgaveIderSomVises)) {
            LOG.debug("Alle oppgaver fortsatt tilgjengelig for sakliste {}: {}", sakslisteId.getVerdi(), oppgaveIderSomVises);
            return Redirect.sendTilPolling(request, sakslisteId, oppgaverIder);
        }
        return Redirect.sendTilResultat(request, sakslisteId);
    }

    @GET
    @Path(OPPGAVER_RESULTAT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent " + ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER
        + " neste oppgaver", tags = "Saksbehandler", responses = {@ApiResponse(responseCode = "200", description = "Returnerer Oppgaver", content = @Content(schema = @Schema(implementation = OppgaveDto.class))),})
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public List<OppgaveDto> getOppgaverTilBehandling(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        var oppgaverTilBehandling = oppgaveDtoTjeneste.getOppgaverTilBehandling(sakslisteId.getVerdi());
        LOG.debug("Oppgaver til behandling for saksliste {}: {}", sakslisteId.getVerdi(), oppgaverTilBehandling);
        return oppgaverTilBehandling;
    }

    @GET
    @Path("/antall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til sakslisten", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveKøTjeneste.hentAntallOppgaver(sakslisteId.getVerdi(), false);
    }

    @GET
    @Path("/oppgaver-for-fagsaker")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til fagsaker", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK, sporingslogg = false)
    public List<OppgaveDto> hentOppgaverForFagsaker(@NotNull @Parameter(description = "Liste med saksnummer") @QueryParam("saksnummerListe") @Valid SaknummerIderDto saksnummerliste) {
        return oppgaveDtoTjeneste.hentOppgaverForFagsaker(saksnummerliste.getSaksnummerListe());
    }
}
