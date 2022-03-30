package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import static no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste.ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveStatusDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveFlyttingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIderDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveOpphevingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.ReservasjonsEndringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.SaknummerIderDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path(OppgaveRestTjeneste.OPPGAVER_BASE_PATH)
@ApplicationScoped
@Transactional
public class OppgaveRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRestTjeneste.class);

    public static final String OPPGAVER_BASE_PATH =  "/saksbehandler/oppgaver";
    public static final String OPPGAVER_STATUS_PATH =  "/status";
    public static final String OPPGAVER_RESULTAT_PATH =  "/resultat";

    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public OppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                               OppgaveKøTjeneste oppgaveKøTjeneste, ReservasjonTjeneste reservasjonTjeneste,
                               OppgaveDtoTjeneste oppgaveDtoTjeneste,
                               SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public OppgaveRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Init hent oppgaver", tags = "Saksbehandler",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Hent oppgaver initiert, Returnerer link til å polle etter nye oppgaver",
                            headers = {@Header(name = "Location")})
            })
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentOppgaver(@Context HttpServletRequest request,
                                 @NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId,
                                 @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaveIder) throws URISyntaxException {
        return Redirect.sendTilStatus(request, sakslisteId, oppgaveIder);
    }

    @GET
    @Path(OPPGAVER_STATUS_PATH)
    @Operation(description = "Url for å polle på oppgaver asynkront", tags = "Saksbehandler",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returnerer Status", content = @Content(schema = @Schema(implementation = AsyncPollingStatus.class))),
                    @ApiResponse(responseCode = "303", description = "Nye oppgaver tilgjenglig", headers = {@Header(name = "Location")})
            })
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.FAGSAK)
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
    @Operation(description = "Hent " + ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER + " neste oppgaver", tags = "Saksbehandler",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returnerer Oppgaver", content = @Content(schema = @Schema(implementation = OppgaveDto.class))),
            })
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.FAGSAK)
    public List<OppgaveDto> getOppgaverTilBehandling(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        var oppgaverTilBehandling = oppgaveDtoTjeneste.getOppgaverTilBehandling(sakslisteId.getVerdi());
        LOG.debug("Oppgaver til behandling for saksliste {}: {}", sakslisteId.getVerdi(), oppgaverTilBehandling);
        return oppgaverTilBehandling;
    }

    @POST
    @Path("/reserver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reserver oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto reserverOppgave(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.reserverOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reservasjon-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent reservasjonsstatus", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto hentReservasjon(@NotNull @Parameter(description = "id til oppgaven") @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.hentReservasjon(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }


    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id og begrunnelse") @Valid OppgaveOpphevingDto opphevetOppgave) {
        var reservasjon = reservasjonTjeneste.slettReservasjon(opphevetOppgave.getOppgaveId().getVerdi(), opphevetOppgave.getBegrunnelse());
        return reservasjon
                .map(res -> oppgaveDtoTjeneste.lagDtoFor(res.getOppgave(), false))
                .map(OppgaveDto::getStatus)
                .orElseGet(() -> {
                    LOG.warn("Fant ikke reservasjon tilknyttet oppgaveId {} for sletting, returnerer null", opphevetOppgave.getOppgaveId());
                    return null;
                });
    }

    @POST
    @Path("/forleng")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Forleng reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto forlengOppgaveReservasjon(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.forlengReservasjonPåOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @POST
    @Path("/reservasjon/endre")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Endre reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto endreOppgaveReservasjon(@NotNull @Parameter(description = "forleng til dato") @Valid ReservasjonsEndringDto reservasjonsEndring) {
        var tidspunkt = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(reservasjonsEndring.getReserverTil());
        var reservasjon = reservasjonTjeneste.endreReservasjonPåOppgave(reservasjonsEndring.getOppgaveId().getVerdi(), tidspunkt);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reserverte")
    @Produces("application/json")
    @Operation(description = "Reserverte", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getReserverteOppgaver() {
        return oppgaveDtoTjeneste.getSaksbehandlersReserverteAktiveOppgaver();
    }

    @GET
    @Path("/behandlede")
    @Produces("application/json")
    @Operation(description = "Behandlede", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getBehandledeOppgaver() {
        return oppgaveDtoTjeneste.getSaksbehandlersSisteReserverteOppgaver();
    }

    @POST
    @Path("/flytt/søk")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saksbehandler som er tilknyttet behandlingskø", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK)
    public SaksbehandlerMedAvdelingerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        var ident = brukerIdent.getVerdi().toUpperCase();
        var saksbehandler = saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(ident);
        return saksbehandler.orElse(null);
    }

    @POST
    @Path("/flytt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Flytt reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto flyttOppgaveReservasjon(@NotNull @Parameter(description = "id, begrunnelse og brukerident") @Valid OppgaveFlyttingDto oppgaveFlyttingDto) {
        var reservasjon = reservasjonTjeneste.flyttReservasjon(oppgaveFlyttingDto.getOppgaveId().getVerdi(),
                oppgaveFlyttingDto.getBrukerIdent().getVerdi(),
                oppgaveFlyttingDto.getBegrunnelse());
        LOG.info("Reservasjon flyttet: {}", oppgaveFlyttingDto);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/antall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til sakslisten", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveKøTjeneste.hentAntallOppgaver(sakslisteId.getVerdi(), false);
    }

    @GET
    @Path("/oppgaver-for-fagsaker")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til fagsaker", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentOppgaverForFagsaker(@NotNull @Parameter(description = "Liste med saksnummer") @QueryParam("saksnummerListe") @Valid SaknummerIderDto saksnummerliste) {
        return oppgaveDtoTjeneste.hentOppgaverForFagsaker(saksnummerliste.getSaksnummerListe());
    }
}
