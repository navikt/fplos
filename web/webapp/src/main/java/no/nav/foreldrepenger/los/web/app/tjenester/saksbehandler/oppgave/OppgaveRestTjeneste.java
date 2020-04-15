package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
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
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.ReservasjonsEndringDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.SaknummerIderDto;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.oppgave.SaksbehandlerinformasjonDto;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Path("/saksbehandler/oppgaver")
@RequestScoped
@Transactional
public class OppgaveRestTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(OppgaveRestTjeneste.class);
    private static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;

    private OppgaveTjeneste oppgaveTjeneste;
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste;
    private TpsTjeneste tpsTjeneste;

    public OppgaveRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public OppgaveRestTjeneste(OppgaveTjeneste oppgaveTjeneste, FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste, TpsTjeneste tpsTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.fagsakApplikasjonTjeneste = fagsakApplikasjonTjeneste;
        this.tpsTjeneste = tpsTjeneste;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Init hent oppgaver", tags = "Saksbehandler",
        responses = {
            @ApiResponse(responseCode = "202", description = "Hent oppgaver initiert, Returnerer link til å polle etter nye oppgaver",
                    headers = { @Header(name = "Location") })
    })
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response hentOppgaver(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId, @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaveIder) throws URISyntaxException {
        URI uri = new URI(("/saksbehandler/oppgaver/status?sakslisteId=" + sakslisteId.getVerdi()) + (oppgaveIder == null ? "" : "&oppgaveIder=" +oppgaveIder.getVerdi()));
        return Response.accepted().location(uri).build();
    }

    @GET
    @Path("/status")
    @Operation(description = "Url for å polle på oppgaver asynkront", tags = "Saksbehandler",
        responses = {
            @ApiResponse(responseCode = "200", description = "Returnerer Status", content = @Content(schema = @Schema(implementation = AsyncPollingStatus.class))),
            @ApiResponse(responseCode = "303", description = "Nye oppgaver tilgjenglig", headers = { @Header(name = "Location") })
    })
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    public Response hentNesteOppgaverOgSjekkOmDisseErNye(@NotNull @Valid @QueryParam("sakslisteId") SakslisteIdDto sakslisteId,
                                                         @Valid @QueryParam("oppgaveIder") OppgaveIderDto oppgaverIder) throws URISyntaxException {
        List<Long> oppgaveIderSomVises = oppgaverIder == null ? Collections.emptyList() : oppgaverIder.getOppgaveIdeer();
        boolean skalPolle = false;

        if (oppgaveIderSomVises.isEmpty()) {
            List<Oppgave> nesteOppgaver = oppgaveTjeneste.hentOppgaver(sakslisteId.getVerdi(), 1);
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
            return Response.status(status.getStatus().getHttpStatus())
                    .entity(status)
                    .build();
        }

        URI uri = new URI("/saksbehandler/oppgaver/resultat?sakslisteId=" +  sakslisteId.getVerdi());
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
        var saksliste = sakslisteId.getVerdi();
        var nesteOppgaver = oppgaveTjeneste.hentOppgaver(saksliste, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
        var oppgaveDtos = map(nesteOppgaver, nesteOppgaver.size());
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (nesteOppgaver.size() == oppgaveDtos.size()) {
            return oppgaveDtos;
        }
        LOGGER.info("{} behandlinger filtrert bort for saksliste {}", nesteOppgaver.size() - oppgaveDtos.size(), saksliste);
        var alleOppgaver = oppgaveTjeneste.hentOppgaver(saksliste);
        return map(alleOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall) {
        List<OppgaveDto> oppgaveDtos = new ArrayList<>();
        for (int i = 0; i < oppgaver.size() && oppgaveDtos.size() < maksAntall; i++) {
            var oppgave = oppgaver.get(i);
            map(oppgave).ifPresent(oppgaveDtos::add);
        }
        return oppgaveDtos;
    }

    private Optional<OppgaveDto> map(Oppgave oppgave) {
        TpsPersonDto personDto;
        try {
            personDto = tpsTjeneste.hentBrukerForAktør(oppgave.getAktorId());
            return Optional.of(new OppgaveDto(oppgave, personDto));
        } catch (HentPersonSikkerhetsbegrensning e) {
            logBegrensning(oppgave, e);
        }
        return Optional.empty();
    }

    @POST
    @Path("/reserver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reserver oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto reserverOppgave(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.reserverOppgave(oppgaveId.getVerdi());
        return lagReservertStatus(reservasjon);
    }

    @GET
    @Path("/reservasjon-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent reservasjonsstatus", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto hentReservasjon(@NotNull @Parameter(description = "id til oppgaven") @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.hentReservasjon(oppgaveId.getVerdi());
        return reservasjon != null && reservasjon.getReservertTil() != null ? lagReservertStatus(reservasjon) : OppgaveStatusDto.ikkeReservert();
    }

    private OppgaveStatusDto lagReservertStatus(@NotNull @Parameter(description = "id til oppgaven") @Valid Reservasjon reservasjon) {
        String navnHvisReservertAvAnnenSaksbehandler = oppgaveTjeneste.hentNavnHvisReservertAvAnnenSaksbehandler(reservasjon);
        String navnHvisFlyttetAvSaksbehandler = oppgaveTjeneste.hentNavnHvisFlyttetAvSaksbehandler(reservasjon.getFlyttetAv());
        return OppgaveStatusDto.reservert(reservasjon, navnHvisReservertAvAnnenSaksbehandler, navnHvisFlyttetAvSaksbehandler);
    }

    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto opphevOppgaveReservasjon(@NotNull @Parameter(description = "Id og begrunnelse") @Valid OppgaveOpphevingDto opphevetOppgave) {
        Reservasjon reservasjon = oppgaveTjeneste.frigiOppgave(opphevetOppgave.getOppgaveId().getVerdi(), opphevetOppgave.getBegrunnelse());
        return OppgaveStatusDto.reservert(reservasjon);
    }

    @POST
    @Path("/forleng")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Forleng reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto forlengOppgaveReservasjon(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        Reservasjon reservasjon = oppgaveTjeneste.forlengReservasjonPåOppgave(oppgaveId.getVerdi());
        return OppgaveStatusDto.reservert(reservasjon);
    }

    @POST
    @Path("/reservasjon/endre")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Endre reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto endreOppgaveReservasjon(@NotNull @Parameter(description = "forleng til dato") @Valid ReservasjonsEndringDto reservasjonsEndring) {
        Reservasjon reservasjon = oppgaveTjeneste.endreReservasjonPåOppgave(reservasjonsEndring.getOppgaveId(), sjekkGrenseverdier(reservasjonsEndring.getReserverTil().atTime(23,59)));
        return OppgaveStatusDto.reservert(reservasjon);
    }

    private LocalDateTime sjekkGrenseverdier(LocalDateTime tidspunkt) throws IllegalArgumentException{
        LocalDateTime now = LocalDateTime.now();
        if(tidspunkt.isBefore(now)) throw new IllegalArgumentException("Reserevasjon kan ikke avsluttes før dagens dato");
        if(tidspunkt.isAfter(now.plusDays(31))) throw new IllegalArgumentException("Reserevasjon kan ikke være lenger enn 30 dager"); //Siden vi bruker LocalDateTime med 23:59 for sjekken så justeres der til påfølgende dag
        return tidspunkt;
    }

    @GET
    @Path("/reserverte")
    @Produces("application/json")
    @Operation(description = "Reserverte", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getReserverteOppgaver() {
        List<Reservasjon> reserveringer = oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver();
        return reserveringer.stream()
                .map(this::oppgaveDtoFra)
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .collect(Collectors.toList());
    }

    private Optional<OppgaveDto> oppgaveDtoFra(Reservasjon reservasjon) {
        var oppgave = reservasjon.getOppgave();
        TpsPersonDto tpsPerson;
        try {
            tpsPerson = tpsTjeneste.hentBrukerForAktør(oppgave.getAktorId());
        } catch (HentPersonSikkerhetsbegrensning e) {
            logBegrensning(oppgave, e);
            return Optional.empty();
        }
        return Optional.of(new OppgaveDto(
                oppgave,
                tpsPerson,
                oppgaveTjeneste.hentNavnHvisFlyttetAvSaksbehandler(reservasjon.getFlyttetAv())));
    }

    private void logBegrensning(Oppgave oppgave, HentPersonSikkerhetsbegrensning e) {
        LOGGER.info("Prøver å slå opp i tps uten å ha tilgang. Ignorerer oppgave {}", oppgave.getId(), e);
    }

    @GET
    @Path("/behandlede")
    @Produces("application/json")
    @Operation(description = "Behandlede", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> getBehandledeOppgaver() {
        List<Oppgave> sistReserverteOppgaver = oppgaveTjeneste.hentSisteReserverteOppgaver();
        return sistReserverteOppgaver.stream()
                .map(o -> oppgaveDtoFra(o.getReservasjon()))
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .collect(Collectors.toList());
    }

    @POST
    @Path("/flytt/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saksbehandler som er tilknyttet behandlingskø", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        String ident = brukerIdent.getVerdi().toUpperCase();
        SaksbehandlerinformasjonDto saksbehandlerInformasjon = oppgaveTjeneste.hentSaksbehandlerNavnOgAvdelinger(ident);
        if (saksbehandlerInformasjon != null) {
            return new SaksbehandlerDto(brukerIdent, saksbehandlerInformasjon.getNavn(), saksbehandlerInformasjon.getAvdelinger());
        } else {
            return null;
        }
    }

    @POST
    @Path("/flytt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Flytt reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveStatusDto flyttOppgaveReservasjon(@NotNull @Parameter(description = "id, begrunnelse og brukerident") @Valid OppgaveFlyttingDto oppgaveFlyttingId) {
        Reservasjon reservasjon = oppgaveTjeneste.flyttReservasjon(oppgaveFlyttingId.getOppgaveId().getVerdi(),
                oppgaveFlyttingId.getBrukerIdent().getVerdi(),
                oppgaveFlyttingId.getBegrunnelse());
        LOGGER.info("Reservasjon flyttet: {}", oppgaveFlyttingId);
        return OppgaveStatusDto.reservert(reservasjon);
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
        List<Oppgave> oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerliste.getSaksnummerListe());
        if (oppgaver.isEmpty()) {
            return Collections.emptyList();
        }

        //Alle fagsakene tilhører samme bruker
        TpsPersonDto personDto;
        try {
            personDto = tpsTjeneste.hentBrukerForAktør(oppgaver.get(0).getAktorId());
        } catch (HentPersonSikkerhetsbegrensning e) {
            logBegrensning(oppgaver.get(0), e);
            return List.of();
        }
        return oppgaver.stream()
                .map(o -> new OppgaveDto(o, personDto, null, fagsakApplikasjonTjeneste.hentNavnHvisReservertAvAnnenSaksbehandler(o)))
                .collect(Collectors.toList());
    }
}
