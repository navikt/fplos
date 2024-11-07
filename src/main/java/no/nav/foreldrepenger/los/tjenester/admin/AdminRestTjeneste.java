package no.nav.foreldrepenger.los.tjenester.admin;

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
import no.nav.foreldrepenger.los.organisasjon.PopulerUidTask;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftAvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.admin.dto.DriftOpprettAvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.admin.dto.EnkelBehandlingIdDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
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
                             OrganisasjonRepository organisasjonRepository, ProsessTaskTjeneste prosessTaskTjeneste) {
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

    @POST
    @Path("/opprett-avdeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Opprett avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response opprettAvdeling(@NotNull @Valid DriftOpprettAvdelingEnhetDto avdelingEnhetDto) {
        organisasjonRepository.opprettEllerReaktiverAvdeling(avdelingEnhetDto.enhetsnummer(), avdelingEnhetDto.enhetsnavn());
        return Response.ok().build();
    }

    @POST
    @Path("/deaktiver-avdeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deaktiverer avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response deaktiverAvdeling(@NotNull @Valid DriftAvdelingEnhetDto avdelingEnhetDto) {
        organisasjonRepository.deaktiverAvdeling(avdelingEnhetDto.avdelingEnhet());
        return Response.ok().build();
    }

    @POST
    @Path("/slett-saksbehandlere")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter saksbehandlere uten knytning til køer eller avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response slettSaksbehandlereUtenKnytninger() {
        organisasjonRepository.slettSaksbehandlereUtenKnytninger();
        return Response.ok().build();
    }

    @POST
    @Path("/byttTilNav")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter saksbehandlere uten knytning til køer eller avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response byttTilNav() {
        organisasjonRepository.nySkrivemåteNav();
        return Response.ok().build();
    }

    @POST
    @Path("/populerUid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter saksbehandlere uten knytning til køer eller avdeling", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response populerUid() {
        organisasjonRepository.hentAlleSaksbehandlere()
            .forEach(s -> {
                var task = ProsessTaskData.forProsessTask(PopulerUidTask.class);
                task.setProperty(PopulerUidTask.SBH_IDENT_KEY, s.getSaksbehandlerIdent());
                prosessTaskTjeneste.lagre(task);
            });
        return Response.ok().build();
    }

}
