package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.OppgaveEventLoggDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.fplos.admin.AdminTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING;

@Path("/admin")
@RequestScoped
@Transactional
public class AdminRestTjeneste {

    private AdminTjeneste adminTjeneste;

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public AdminRestTjeneste(AdminTjeneste adminTjeneste, ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient) {
        this.adminTjeneste = adminTjeneste;
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
    }

    @GET
    @Path("/synkroniseroppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Synkroniser oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto synkroniserOppgave(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        UUID uuid = behandlingIdDto.getUuid();
        if(uuid == null){
            Long behandlingsId = behandlingIdDto.getBehandlingId();
            if(behandlingsId == null) {
                throw new IllegalArgumentException("Finner ingen gyldig verdi for behandlingsId");
            }
            Optional<UUID> eksternId = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingsId);
            uuid = eksternId.orElseThrow(() -> new IllegalArgumentException("Finner ikke uuid for behandlingsId " + behandlingsId));
        }
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(uuid);
        return new OppgaveDto(oppgave);
    }

    @GET
    @Path("/sepaaoppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Se på oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto hentOppgave(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        UUID uuid = behandlingIdDto.getUuid();
        if(uuid == null){
            Long behandlingsId = behandlingIdDto.getBehandlingId();
            if(behandlingsId == null) {
                throw new IllegalArgumentException("Finner ingen gyldig verdi for behandlingsId");
            }
            Optional<UUID> eksternId = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingsId);
            uuid = eksternId.orElseThrow(() -> new IllegalArgumentException("Finner ikke uuid for behandlingsId " + behandlingsId));
        }
        Oppgave oppgave = adminTjeneste.hentOppgave(uuid);
        return oppgave != null ? new OppgaveDto(oppgave) : null;
    }

    @GET
    @Path("/sepaaeventer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Se på oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveEventLoggDto> hentEventlogg(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        UUID uuid = behandlingIdDto.getUuid();
        if(uuid == null){
            Long behandlingsId = behandlingIdDto.getBehandlingId();
            if(behandlingsId == null) {
                throw new IllegalArgumentException("Finner ingen gyldig verdi for behandlingsId");
            }
            Optional<UUID> eksternId = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingsId);
            uuid = eksternId.orElseThrow(() -> new IllegalArgumentException("Finner ikke uuid for behandlingsId " + behandlingsId));
        }
        List<OppgaveEventLogg> oppgaveEventLogger = adminTjeneste.hentEventer(uuid);
        return oppgaveEventLogger.stream().map(o -> new OppgaveEventLoggDto(o)).collect(Collectors.toList());
    }

    @GET
    @Path("/oppdateringavoppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppdater oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto oppdaterOppgave(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        UUID uuid = behandlingIdDto.getUuid();
        if(uuid == null){
            Long behandlingsId = behandlingIdDto.getBehandlingId();
            if(behandlingsId == null) {
                throw new IllegalArgumentException("Finner ingen gyldig verdi for behandlingsId");
            }
            Optional<UUID> eksternId = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingsId);
            uuid = eksternId.orElseThrow(() -> new IllegalArgumentException("Finner ikke uuid for behandlingsId " + behandlingsId));
        }
        adminTjeneste.oppdaterOppgave(uuid);
        Oppgave oppgave = adminTjeneste.hentOppgave(uuid);
        return new OppgaveDto(oppgave);
    }

    @GET
    @Path("/prosesser-melding")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Prosesser alle meldinger på feilkø", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public int prosesserMelding() {
        return adminTjeneste.prosesserAlleMeldingerFraFeillogg();
    }

    @GET
    @Path("/hent-alle-oppgaver-knyttet-til-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter ut alle oppgaver knyttet til behandling", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentAlleOppgaverForBehandling(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        UUID uuid = behandlingIdDto.getUuid();
        if(uuid == null){
            Long behandlingsId = behandlingIdDto.getBehandlingId();
            if(behandlingsId == null) {
                throw new IllegalArgumentException("Finner ingen gyldig verdi for behandlingsId");
            }
            Optional<UUID> eksternId = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingsId);
            uuid = eksternId.orElseThrow(() -> new IllegalArgumentException("Finner ikke uuid for behandlingsId " + behandlingsId));
        }
        List<Oppgave> oppgaver = adminTjeneste.hentAlleOppgaverForBehandling(uuid);
        return oppgaver.stream().map(OppgaveDto::new).collect(Collectors.toList());
    }

    @GET
    @Path("/deaktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deaktiver oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto deaktiverOppgave(@NotNull @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveIdDto) {
        Oppgave oppgave = adminTjeneste.deaktiverOppgave(oppgaveIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }

    @GET
    @Path("/aktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Aktiver oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto aktiverOppgave(@NotNull @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveIdDto) {
        Oppgave oppgave = adminTjeneste.aktiverOppgave(oppgaveIdDto.getVerdi());
        return new OppgaveDto(oppgave);
    }
}
