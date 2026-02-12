package no.nav.foreldrepenger.los.tjenester.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.statistikk.SnapshotEnhetYtelseBehandlingTask;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftAvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftOpprettAvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.admin.dto.EnkelBehandlingIdDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.felles.prosesstask.api.TaskType;
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
    private OrganisasjonRepository organisasjonRepository;
    private ProsessTaskTjeneste prosessTaskTjeneste;

    @Inject
    public AdminRestTjeneste(SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste,
                             OppgaveTjeneste oppgaveTjeneste,
                             OrganisasjonRepository organisasjonRepository,
                             ProsessTaskTjeneste prosessTaskTjeneste) {
        this.synkroniseringHendelseTaskOppretterTjeneste = synkroniseringHendelseTaskOppretterTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.organisasjonRepository = organisasjonRepository;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
    }

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @POST
    @Path("/synkroniser-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppretter task for synkronisering av behandling med fpsak", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response synkroniserHendelser(@NotNull List<@Valid EnkelBehandlingIdDto> behandlingIdListe) {
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
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response synkroniserHendelserTilbake(@NotNull List<@Valid EnkelBehandlingIdDto> behandlingIdListe) {
        var behandlinger = behandlingIdListe.stream()
            .map(EnkelBehandlingIdDto::getBehandlingId)
            .map(b -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPTILBAKE, b))
            .toList();
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTasks(behandlinger);
        return Response.ok(opprettedeTasker).build();
    }

    @POST
    @Path("/behold-kun-en-aktiv")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Setter tidligste inaktiv der flere aktive", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response slettTidligsteMultiAktiv(@NotNull @Valid EnkelBehandlingIdDto behandlingId) {
        var behandlinger = behandlingId.getBehandlingId();
        oppgaveTjeneste.adminAvsluttMultiOppgaveAvsluttTilknyttetReservasjon(behandlinger);
        return Response.ok().build();
    }

    @POST
    @Path("/opprett-avdeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opprett avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response opprettAvdeling(@NotNull @Valid DriftOpprettAvdelingEnhetDto avdelingEnhetDto) {
        organisasjonRepository.opprettEllerReaktiverAvdeling(avdelingEnhetDto.enhetsnummer(), avdelingEnhetDto.enhetsnavn());
        return Response.ok().build();
    }

    @POST
    @Path("/deaktiver-avdeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deaktiverer avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response deaktiverAvdeling(@NotNull @Valid DriftAvdelingEnhetDto avdelingEnhetDto) {
        organisasjonRepository.deaktiverAvdeling(avdelingEnhetDto.avdelingEnhet());
        return Response.ok().build();
    }

    @POST
    @Path("/slett-saksbehandlere")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter saksbehandlere uten knytning til køer eller avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response slettSaksbehandlereUtenKnytninger() {
        organisasjonRepository.slettSaksbehandlereUtenKnytninger();
        return Response.ok().build();
    }

    @POST
    @Path("/slett-lose-gruppe-knytninger")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjerne saksbehandlere fra grupper når saksbehandler mangler i avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response slettLøseGruppeKnytninger() {
        organisasjonRepository.slettLøseGruppeKnytninger();
        return Response.ok().build();
    }

    @POST
    @Path("/start-statistikktask-ytelse-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjerne saksbehandlere fra grupper når saksbehandler mangler i avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response startStatistikkTaskYtelseBehandling() {
        var schedulerType = TaskType.forProsessTask(SnapshotEnhetYtelseBehandlingTask.class);
        var eksisterende = prosessTaskTjeneste.finnAlle(ProsessTaskStatus.KLAR).stream()
            .map(ProsessTaskData::taskType)
            .anyMatch(schedulerType::equals);
        if (!eksisterende) {
            var taskData = ProsessTaskData.forProsessTask(SnapshotEnhetYtelseBehandlingTask.class);
            taskData.setNesteKjøringEtter(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 0)));
            prosessTaskTjeneste.lagre(taskData);
        }
        return Response.ok().build();
    }

    @POST
    @Path("/oppdater-saksbehandler-navn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjerne saksbehandlere fra grupper når saksbehandler mangler i avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response populerSaksbehandlereNavn() {
        var t = ProsessTaskData.forProsessTask(OppdaterSaksbehandlerTask.class);
        t.setProperty(OppdaterSaksbehandlerTask.NOREPEAT, OppdaterSaksbehandlerTask.NOREPEAT);
        prosessTaskTjeneste.lagre(t);
        return Response.ok().build();
    }

    @POST
    @Path("/slett-saksbehandler-sluttet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjerne saksbehandlere som har sluttet", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT, sporingslogg = false)
    public Response fjerneSaksbehandlereSluttet() {
        var slettet = organisasjonRepository.fjernSaksbehandlereSomHarSluttet();
        return Response.ok(slettet).build();
    }

}
