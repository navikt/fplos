package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.los.admin.SynkroniseringHendelseTaskOppretterTjeneste;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.EnkelBehandlingIdDto;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/admin")
@ApplicationScoped
@Transactional
public class AdminRestTjeneste {

    private SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveRepository oppgaveRepository;

    @Inject
    public AdminRestTjeneste(SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste,
                             OppgaveTjeneste oppgaveTjeneste, OppgaveRepository oppgaveRepository) {
        this.synkroniseringHendelseTaskOppretterTjeneste = synkroniseringHendelseTaskOppretterTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveRepository = oppgaveRepository;
    }

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @POST
    @Path("/synkroniser-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppretter task for synkronisering av behandling med fpsak", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response synkroniserHendelser(@NotNull @Valid List<EnkelBehandlingIdDto> behandlingIdListe) {
        var behandlinger = behandlingIdListe.stream()
            .map(EnkelBehandlingIdDto::getBehandlingId)
            .map(b -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPSAK, b))
            .toList();
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTasks(behandlinger);
        return Response.ok(opprettedeTasker).build();
    }

    @POST
    @Path("/synkroniser-behandling-fptilbake")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppretter task for synkronisering av behandling med fptilbake", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response synkroniserHendelserTilbake(@NotNull @Valid List<EnkelBehandlingIdDto> behandlingIdListe) {
        var behandlinger = behandlingIdListe.stream()
            .map(EnkelBehandlingIdDto::getBehandlingId)
            .map(b -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPTILBAKE, b))
            .toList();
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTasks(behandlinger);
        return Response.ok(opprettedeTasker).build();
    }

    @POST
    @Path("/synkroniser-aapne-revurderinger")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppretter task for synkronisering av behandling med fpsak", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response synkroniserAapneRevurderinger() {
        var behandlinger = oppgaveRepository.hentAktiveRevurderingOppgaverMedStønadsdatoFør(LocalDate.now().minusMonths(3)).stream()
            .map(Oppgave::getBehandlingId)
            .map(b -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPSAK, b))
            .toList();
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTasks(behandlinger);
        return Response.ok(opprettedeTasker).build();
    }

    @POST
    @Path("/behold-kun-en-aktiv")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Setter tidligste inaktiv der flere aktive", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response slettTidligsteMultiAktiv(@NotNull @Valid EnkelBehandlingIdDto behandlingId) {
        var behandlinger = behandlingId.getBehandlingId();
        oppgaveTjeneste.adminAvsluttMultiOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlinger);
        return Response.ok().build();
    }

}
