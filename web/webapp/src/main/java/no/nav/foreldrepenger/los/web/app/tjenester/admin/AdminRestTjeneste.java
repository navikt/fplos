package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.OppgaveEventLoggDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.fplos.admin.AdminTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING;

@Api(tags = { "Admin" })
@Path("/admin")
@RequestScoped
@Transaction
public class AdminRestTjeneste {

    private AdminTjeneste adminTjeneste;

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public AdminRestTjeneste(AdminTjeneste adminTjeneste) {
        this.adminTjeneste = adminTjeneste;
    }

    @GET
    @Timed
    @Path("/synkroniseroppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Synkroniser oppgave", notes = ("Synkroniser oppgave"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto synkroniserOppgave(@NotNull @QueryParam("behandlingIdDto") @Valid BehandlingIdDto behandlingIdDto) {
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(behandlingIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }

    @GET
    @Timed
    @Path("/sepaaoppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Se på oppgave", notes = ("See informasjon om oppgave"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto hentOppgave(@NotNull @QueryParam("behandlingIdDto") @Valid BehandlingIdDto behandlingIdDto) {
        Oppgave oppgave = adminTjeneste.hentOppgave(behandlingIdDto.getVerdi());
        return oppgave != null ? new OppgaveDto(oppgave) : null;
    }

    @GET
    @Timed
    @Path("/sepaaeventer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Se på oppgave", notes = ("See informasjon om oppgave"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveEventLoggDto> hentEventlogg(@NotNull @QueryParam("behandlingIdDto") @Valid BehandlingIdDto behandlingIdDto) {
        List<OppgaveEventLogg> oppgaveEventLogger = adminTjeneste.hentEventer(behandlingIdDto.getVerdi());
        return oppgaveEventLogger.stream().map(o -> new OppgaveEventLoggDto(o)).collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("/oppdateringavoppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Oppdater oppgave", notes = ("Full oppdatering av oppgave"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto oppdaterOppgave(@NotNull @QueryParam("behandlingIdDto") @Valid BehandlingIdDto behandlingIdDto) {
        adminTjeneste.oppdaterOppgave(behandlingIdDto.getVerdi());
        Oppgave oppgave = adminTjeneste.hentOppgave(behandlingIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }

    @GET
    @Timed
    @Path("/oppdatering-av-alle-aktive-oppgaver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Oppdater alle aktive oppgaver", notes = ("Full oppdatering av alle aktive oppgaver. Returnerer antall oppgaver som ble oppdatert"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public int oppdaterAlleAktiveOppgaver() {
        return adminTjeneste.oppdaterAktiveOppgaver();
    }

    @GET
    @Timed
    @Path("/prosesser-melding")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Prosesser alle meldinger på feilkø", notes = ("Prosesserer meldinger som har kommet over Kafka-køen men som av ulike årsaker ikke har blitt prosessert riktig"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public int prosesserMelding() {
        return adminTjeneste.prosesserAlleMeldingerFraFeillogg();
    }

    @GET
    @Timed
    @Path("/hent-alle-oppgaver-knyttet-til-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Henter ut alle oppgaver knyttet til behandling", notes = ("Angi behandlingId for å se alle oppgaver tilknyttet behandlingen"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentAlleOppgaverForBehandling(@NotNull @QueryParam("behandlingIdDto") @Valid BehandlingIdDto behandlingIdDto) {
        List<Oppgave> oppgaver = adminTjeneste.hentAlleOppgaverForBehandling(behandlingIdDto.getVerdi());
        return oppgaver.stream().map(OppgaveDto::new).collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("/deaktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Deaktiver oppgave", notes = ("Setter aktiv=N i Oppgave for angitt oppgaveId"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto deaktiverOppgave(@NotNull @QueryParam("oppgaveIdDto") @Valid OppgaveIdDto oppgaveIdDto) {
        Oppgave oppgave = adminTjeneste.deaktiverOppgave(oppgaveIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }

    @GET
    @Timed
    @Path("/aktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Aktiver oppgave", notes = ("Setter aktiv=J i Oppgave for angitt oppgaveId"))
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto aktiverOppgave(@NotNull @QueryParam("oppgaveIdDto") @Valid OppgaveIdDto oppgaveIdDto) {
        Oppgave oppgave = adminTjeneste.aktiverOppgave(oppgaveIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }
}
