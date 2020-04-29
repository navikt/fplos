package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import static no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste.ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveStatusDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveFlyttingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIderDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveOpphevingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.ReservasjonsEndringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.SaknummerIderDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Path("/saksbehandler/oppgaver")
@ApplicationScoped
@Transactional
public class OppgaveRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveRestTjeneste.class);
    private static final int POLL_INTERVAL_MILLIS = 1000;

    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public OppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                               OppgaveDtoTjeneste oppgaveDtoTjeneste,
                               SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
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
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentOppgaver(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId, @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaveIder) throws URISyntaxException {
        URI uri = new URI(("/saksbehandler/oppgaver/status?sakslisteId=" + sakslisteId.getVerdi()) + (oppgaveIder == null ? "" : "&oppgaveIder=" + oppgaveIder.getVerdi()));
        return Response.accepted().location(uri).build();
    }

    @GET
    @Path("/status")
    @Operation(description = "Url for å polle på oppgaver asynkront", tags = "Saksbehandler",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returnerer Status", content = @Content(schema = @Schema(implementation = AsyncPollingStatus.class))),
                    @ApiResponse(responseCode = "303", description = "Nye oppgaver tilgjenglig", headers = {@Header(name = "Location")})
            })
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    public Response hentNesteOppgaverOgSjekkOmDisseErNye(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId,
                                                         @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaverIder) throws URISyntaxException {
        List<Long> oppgaveIderSomVises = oppgaverIder == null ? List.of() : oppgaverIder.getOppgaveIdeer();
        boolean skalPolle = false;

        if (oppgaveIderSomVises.isEmpty()) {
            skalPolle = !oppgaveDtoTjeneste.harTilgjengeligeOppgaver(sakslisteId);
            if (!skalPolle) {
                URI uri = new URI("/saksbehandler/oppgaver/resultat?sakslisteId=" + sakslisteId.getVerdi());
                return Response.seeOther(uri).build();
            }
        }

        if (skalPolle || !oppgaveTjeneste.harForandretOppgaver(oppgaveIderSomVises)) {
            String ider = oppgaverIder != null ? oppgaverIder.getVerdi() : "";
            URI uri = new URI("/saksbehandler/oppgaver/status?sakslisteId=" + sakslisteId.getVerdi() + "&oppgaveIder=" + ider);
            AsyncPollingStatus status = new AsyncPollingStatus(AsyncPollingStatus.Status.PENDING, "", POLL_INTERVAL_MILLIS);
            status.setLocation(uri);
            return Response.status(status.getStatus().getHttpStatus())
                    .entity(status)
                    .build();
        }

        URI uri = new URI("/saksbehandler/oppgaver/resultat?sakslisteId=" + sakslisteId.getVerdi());
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/resultat")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent " + ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER + " neste oppgaver", tags = "Saksbehandler",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returnerer Oppgaver", content = @Content(schema = @Schema(implementation = OppgaveDto.class))),
            })
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    public List<OppgaveDto> getOppgaverTilBehandling(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveDtoTjeneste.getOppgaverTilBehandling(sakslisteId.getVerdi());
    }

    @POST
    @Path("/reserver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reserver oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto reserverOppgave(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = oppgaveTjeneste.reserverOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reservasjon-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent reservasjonsstatus", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto hentReservasjon(@NotNull @Parameter(description = "id til oppgaven") @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = oppgaveTjeneste.hentReservasjon(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }


    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id og begrunnelse") @Valid OppgaveOpphevingDto opphevetOppgave) {
        var reservasjon = oppgaveTjeneste.frigiOppgave(opphevetOppgave.getOppgaveId().getVerdi(), opphevetOppgave.getBegrunnelse());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @POST
    @Path("/forleng")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Forleng reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto forlengOppgaveReservasjon(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = oppgaveTjeneste.forlengReservasjonPåOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @POST
    @Path("/reservasjon/endre")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Endre reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto endreOppgaveReservasjon(@NotNull @Parameter(description = "forleng til dato") @Valid ReservasjonsEndringDto reservasjonsEndring) {
        var tidspunkt = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(reservasjonsEndring.getReserverTil());
        var reservasjon = oppgaveTjeneste.endreReservasjonPåOppgave(reservasjonsEndring.getOppgaveId().getVerdi(), tidspunkt);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reserverte")
    @Produces("application/json")
    @Operation(description = "Reserverte", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getReserverteOppgaver() {
        return oppgaveDtoTjeneste.getReserverteOppgaver();
    }

    @GET
    @Path("/behandlede")
    @Produces("application/json")
    @Operation(description = "Behandlede", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getBehandledeOppgaver() {
        return oppgaveDtoTjeneste.getBehandledeOppgaver();
    }

    @POST
    @Path("/flytt/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saksbehandler som er tilknyttet behandlingskø", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    public SaksbehandlerMedAvdelingerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        String ident = brukerIdent.getVerdi().toUpperCase();
        var saksbehandler = saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(ident);
        return saksbehandler.orElse(null);
    }

    @POST
    @Path("/flytt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Flytt reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto flyttOppgaveReservasjon(@NotNull @Parameter(description = "id, begrunnelse og brukerident") @Valid OppgaveFlyttingDto oppgaveFlyttingId) {
        var reservasjon = oppgaveTjeneste.flyttReservasjon(oppgaveFlyttingId.getOppgaveId().getVerdi(),
                oppgaveFlyttingId.getBrukerIdent().getVerdi(),
                oppgaveFlyttingId.getBegrunnelse());
        LOG.info("Reservasjon flyttet: {}", oppgaveFlyttingId);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/antall")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til sakslisten", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveTjeneste.hentAntallOppgaver(sakslisteId.getVerdi(), false);
    }

    @GET
    @Path("/oppgaver-for-fagsaker")
    @Produces("application/json")
    @Operation(description = "Henter antall oppgaver knyttet til fagsaker", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentOppgaverForFagsaker(@NotNull @Parameter(description = "Liste med saksnummer") @QueryParam("saksnummerListe") @Valid SaknummerIderDto saksnummerliste) {
        return oppgaveDtoTjeneste.hentOppgaverForFagsaker(saksnummerliste.getSaksnummerListe());
    }
}
