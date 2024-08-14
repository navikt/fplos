package no.nav.foreldrepenger.los.tjenester.reservasjon;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.tjenester.felles.dto.ReservasjonStatusDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.tjenester.reservasjon.dto.ReservasjonEndringRequestDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveFlyttingDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OpphevTilknyttetReservasjonRequestDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path(no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste.OPPGAVER_BASE_PATH)
@ApplicationScoped
@Transactional
public class TempReservasjonRestTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(TempReservasjonRestTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public TempReservasjonRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                                       ReservasjonTjeneste reservasjonTjeneste,
                                       OppgaveDtoTjeneste oppgaveDtoTjeneste,
                                       SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public TempReservasjonRestTjeneste() {
        // For Rest-CDI
    }


    @POST
    @Path("/reserver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reserver oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto reserverOppgave(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.reserverOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reservasjon-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Hent reservasjonsstatus", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto hentReservasjon(@NotNull @Parameter(description = "id til oppgaven") @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveId) {
        var oppgave = oppgaveTjeneste.hentOppgave(oppgaveId.getVerdi());
        var oppgaveDto = oppgaveDtoTjeneste.lagDtoFor(oppgave, false);
        return oppgaveDto.getStatus();
    }


    @POST
    @Path("/opphev")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opphev reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto opphevReservasjonTilknyttetOppgave(@NotNull @Parameter(description = "Id og begrunnelse") @Valid OpphevTilknyttetReservasjonRequestDto request) {
        var reservasjon = reservasjonTjeneste.slettReservasjonMedEventLogg(request.getOppgaveId().getVerdi(), request.getBegrunnelse());
        return reservasjon.map(Reservasjon::getOppgave).map(oppgaveDtoTjeneste::lagOppgaveStatusUtenTilgangsjekk).orElseGet(() -> {
            LOG.info("Fant ikke reservasjon tilknyttet oppgaveId {} for sletting, returnerer null", request.getOppgaveId());
            return null;
        });
    }

    @POST
    @Path("/forleng")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Forleng reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto forlengOppgaveReservasjon(@NotNull @Parameter(description = "id til oppgaven") @Valid OppgaveIdDto oppgaveId) {
        var reservasjon = reservasjonTjeneste.forlengReservasjonPåOppgave(oppgaveId.getVerdi());
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @POST
    @Path("/reservasjon/endre")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Endre reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto endreOppgaveReservasjon(@NotNull @Parameter(description = "forleng til dato") @Valid ReservasjonEndringRequestDto reservasjonsEndring) {
        var tidspunkt = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(reservasjonsEndring.reserverTil());
        var reservasjon = reservasjonTjeneste.endreReservasjonPåOppgave(reservasjonsEndring.oppgaveId().getVerdi(), tidspunkt);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/reserverte")
    @Produces("application/json")
    @Operation(description = "Reserverte", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public List<OppgaveDto> getReserverteOppgaver() {
        return oppgaveDtoTjeneste.getSaksbehandlersReserverteAktiveOppgaver();
    }

    @POST
    @Path("/flytt/søk")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saksbehandler som er tilknyttet behandlingskø", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        var ident = brukerIdent.getVerdi().toUpperCase();
        var saksbehandler = saksbehandlerDtoTjeneste.hentSaksbehandlerTilknyttetMinstEnKø(ident);
        return saksbehandler.orElse(null);
    }

    @POST
    @Path("/flytt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Flytt reservasjon av oppgave", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.FAGSAK)
    public ReservasjonStatusDto flyttOppgaveReservasjon(@NotNull @Parameter(description = "id, begrunnelse og brukerident") @Valid OppgaveFlyttingDto oppgaveFlyttingDto) {
        var reservasjon = reservasjonTjeneste.flyttReservasjon(oppgaveFlyttingDto.getOppgaveId().getVerdi(),
            oppgaveFlyttingDto.getBrukerIdent().getVerdi(), oppgaveFlyttingDto.getBegrunnelse());
        LOG.info("Reservasjon flyttet: {}", oppgaveFlyttingDto);
        return oppgaveDtoTjeneste.lagDtoFor(reservasjon.getOppgave(), false).getStatus();
    }

    @GET
    @Path("/behandlede")
    @Produces("application/json")
    @Operation(description = "Behandlede", tags = "Saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public List<OppgaveDto> getBehandledeOppgaver() {
        return oppgaveDtoTjeneste.getSaksbehandlersSisteReserverteOppgaver();
    }


}
