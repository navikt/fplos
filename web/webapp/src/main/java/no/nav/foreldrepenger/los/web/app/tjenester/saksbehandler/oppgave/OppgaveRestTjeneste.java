package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveStatusDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveFlyttingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIderDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveOpphevingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.SaknummerIderDto;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.oppgave.SaksbehandlerinformasjonDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

@Api(tags = { "Saksbehandler" })
@Path("/saksbehandler/oppgaver")
@RequestScoped
@Transaction
public class OppgaveRestTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(OppgaveRestTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste;

    public OppgaveRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public OppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste, FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.fagsakApplikasjonTjeneste = fagsakApplikasjonTjeneste;
    }

    @GET
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Init hent oppgaver")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Hent oppgaver initiert, Returnerer link til å polle etter nye oppgaver", responseHeaders = {
                    @ResponseHeader(name = "Location") })
    })
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentOppgaver(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId, @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaveIder) throws URISyntaxException {
        URI uri = new URI(("/saksbehandler/oppgaver/status?sakslisteId=" + sakslisteId.getVerdi()) + (oppgaveIder == null ? "" : "&oppgaveIder=" +oppgaveIder.getVerdi()));
        return Response.accepted().location(uri).build();
    }

    @GET
    @Path("/status")
    @Timed
    @ApiOperation(value = "Url for å polle på oppgaver asynkront", notes = ("Returnerer link til enten samme (hvis ikke ferdig) eller redirecter til /saksbehandler/oppgaver dersom nye oppgaver er tilgjengelig."))
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returnerer Status", response = AsyncPollingStatus.class),
            @ApiResponse(code = 303, message = "Nye oppgaver tilgjenglig", responseHeaders = { @ResponseHeader(name = "Location") })
    })
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    public Response hentNesteOppgaverOgSjekkOmDisseErNye(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId, @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaverIder) throws URISyntaxException {
        List<Long> oppgaveIderSomVises = oppgaverIder == null ? Collections.emptyList() : oppgaverIder.getOppgaveIdeer();
        boolean skalPolle = false;

        if (oppgaveIderSomVises.isEmpty()) {
            List<Oppgave> nesteOppgaver = oppgaveTjeneste.hentNesteOppgaver(sakslisteId.getVerdi());
            skalPolle = nesteOppgaver.isEmpty();
            if (!skalPolle) {
                URI uri = new URI("/saksbehandler/oppgaver/resultat?sakslisteId=" + sakslisteId.getVerdi());
                return Response.seeOther(uri).build();
            }
        }

        if (skalPolle || !oppgaveTjeneste.harForandretOppgaver(oppgaveIderSomVises)) {
            String ider = oppgaverIder != null ? oppgaverIder.getVerdi() : "";
            URI uri = new URI("/saksbehandler/oppgaver/status?sakslisteId=" +  sakslisteId.getVerdi() + "&oppgaveIder=" + ider);
            AsyncPollingStatus status = new AsyncPollingStatus(AsyncPollingStatus.Status.PENDING, "", 1000);
            status.setLocation(uri);
            return Response.status(status.getStatus().getHttpStatus()).entity(status).build();
        }

        URI uri = new URI("/saksbehandler/oppgaver/resultat?sakslisteId=" +  sakslisteId.getVerdi());
        return Response.seeOther(uri).build();
    }


    @GET
    @Path("/resultat")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hent 3 neste oppgaver", notes = ("Henter 3 neste oppgaver til behandling"))
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returnerer Oppgaver", response = OppgaveDto.class),
    })
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    public List<OppgaveDto> getOppgaverTilBehandling(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        List<Oppgave> nesteOppgaver = oppgaveTjeneste.hentNesteOppgaver(sakslisteId.getVerdi());
        List<OppgaveDto> oppgaveDtos = new ArrayList<>();
        int funnetOppgaver = 0;
        for (int i = 0; i < nesteOppgaver.size() && funnetOppgaver < 3; i++) {
            Oppgave oppgave = nesteOppgaver.get(i);
            Optional<TpsPersonDto> personDto = oppgaveTjeneste.hentPersonInfoOptional(oppgave.getAktorId());
            if (personDto.isPresent()) {
                oppgaveDtos.add(new OppgaveDto(oppgave, personDto.get()));
                funnetOppgaver++;
            }
        }
        return oppgaveDtos;
    }

    @POST
    @Timed
    @Path("/reserver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Reserver oppgave", notes = ("Reserver oppgave"))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto reserverOppgave(@NotNull @ApiParam("id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.reserverOppgave(oppgaveId.getVerdi());
        return lagReservertStatus(reservasjon);
    }

    @GET
    @Timed
    @Path("/reservasjon-status")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Hent reservasjonsstatus")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto hentReservasjon(@NotNull @ApiParam("id til oppgaven") @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.hentReservasjon(oppgaveId.getVerdi());
        return reservasjon != null && reservasjon.getReservertTil() != null ? lagReservertStatus(reservasjon) : OppgaveStatusDto.ikkeReservert();
    }

    private OppgaveStatusDto lagReservertStatus(@NotNull @ApiParam("id til oppgaven") @Valid Reservasjon reservasjon) {
        String navnHvisReservertAvAnnenSaksbehandler = oppgaveTjeneste.hentNavnHvisReservertAvAnnenSaksbehandler(reservasjon);
        String navnHvisFlyttetAvSaksbehandler = oppgaveTjeneste.hentNavnHvisFlyttetAvSaksbehandler(reservasjon.getFlyttetAv());
        return OppgaveStatusDto.reservert(reservasjon, navnHvisReservertAvAnnenSaksbehandler, navnHvisFlyttetAvSaksbehandler);
    }

    @POST
    @Timed
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Opphev reservasjon av oppgave", notes = ("Opphev reservasjon av oppgave"))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @ApiParam("Id og begrunnelse") @Valid OppgaveOpphevingDto opphevetOppgave) {
        Reservasjon reservasjon = oppgaveTjeneste.frigiOppgave(opphevetOppgave.getOppgaveId().getVerdi(), opphevetOppgave.getBegrunnelse());
        return OppgaveStatusDto.reservert(reservasjon);
    }

    @POST
    @Timed
    @Path("/forleng")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Forleng reservasjon av oppgave", notes = ("Forleng reservasjon av oppgave"))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto forlengOppgaveReservasjon(@NotNull @ApiParam("id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.forlengReservasjonPåOppgave(oppgaveId.getVerdi());
        return OppgaveStatusDto.reservert(reservasjon);
    }

    @GET
    @Timed
    @Path("/reserverte")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getReserverteOppgaver() {
        List<Reservasjon> reserveringer = oppgaveTjeneste.hentReserverteOppgaver();
        return reserveringer.stream()
                .map(this::oppgaveDtoFra)
                .collect(Collectors.toList());
    }

    private OppgaveDto oppgaveDtoFra(Reservasjon reservasjon) {
        return new OppgaveDto(reservasjon.getOppgave(),
                oppgaveTjeneste.hentPersonInfo(reservasjon.getOppgave().getAktorId()),
                null,
                oppgaveTjeneste.hentNavnHvisFlyttetAvSaksbehandler(reservasjon.getFlyttetAv()));
    }

    @GET
    @Timed
    @Path("/behandlede")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getBehandledeOppgaver() {
        List<Oppgave> sistReserverteOppgaver = oppgaveTjeneste.hentSisteReserverteOppgaver();
        return sistReserverteOppgaver.stream().map(o -> new OppgaveDto(o, oppgaveTjeneste.hentPersonInfo(o.getAktorId()), null, oppgaveTjeneste.hentNavnHvisFlyttetAvSaksbehandler(o.getReservasjon().getFlyttetAv()))).collect(Collectors.toList());
    }

    @POST
    @Timed
    @Path("/flytt/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Søk etter saksbehandler som er tilknyttet behandlingskø", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @ApiParam("Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        String ident = brukerIdent.getVerdi().toUpperCase();
        SaksbehandlerinformasjonDto saksbehandlerInformasjon = oppgaveTjeneste.hentSaksbehandlerNavnOgAvdelinger(ident);
        if (saksbehandlerInformasjon != null) {
            return new SaksbehandlerDto(brukerIdent, saksbehandlerInformasjon.getNavn(), saksbehandlerInformasjon.getAvdelinger());
        } else {
            return null;
        }
    }

    @POST
    @Timed
    @Path("/flytt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Flytt reservasjon av oppgave", notes = ("Flytt reservasjon av oppgave"))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto flyttOppgaveReservasjon(@NotNull @ApiParam("id, begrunnelse og brukerident") @Valid OppgaveFlyttingDto oppgaveFlyttingId) {
        Reservasjon reservasjon = oppgaveTjeneste.flyttReservasjon(oppgaveFlyttingId.getOppgaveId().getVerdi(),
                oppgaveFlyttingId.getBrukerIdent().getVerdi(),
                oppgaveFlyttingId.getBegrunnelse());
        LOGGER.info("Reservasjon flyttet: {}", oppgaveFlyttingId);
        return OppgaveStatusDto.reservert(reservasjon);
    }

    @GET
    @Timed
    @Path("/antall")
    @Produces("application/json")
    @ApiOperation(value = "Henter antall oppgaver knyttet til sakslisten")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Integer hentAntallOppgaverForSaksliste(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveTjeneste.hentAntallOppgaver(sakslisteId.getVerdi());
    }


    @GET
    @Timed
    @Path("/oppgaver-for-fagsaker")
    @Produces("application/json")
    @ApiOperation(value = "Henter antall oppgaver knyttet til fagsaker")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentOppgaverForFagsaker(@NotNull @ApiParam("Liste med saksnummer") @QueryParam("saksnummerListe") @Valid SaknummerIderDto saksnummerliste) {
        List<Oppgave> oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerliste.getSaksnummerListe());
        if (oppgaver.isEmpty()) {
            return Collections.emptyList();
        }

        //Alle fagsakene tilhører samme bruker
        TpsPersonDto personDto = oppgaveTjeneste.hentPersonInfo(oppgaver.get(0).getAktorId());
        return oppgaver.stream()
                .map(o -> new OppgaveDto(o, personDto, fagsakApplikasjonTjeneste.hentNavnHvisReservertAvAnnenSaksbehandler(o), null))
                .collect(Collectors.toList());
    }
}