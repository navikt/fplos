package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.admin.SynkroniseringHendelseTaskOppretterTjeneste;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.MottattHendelseRepository;
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
    private MottattHendelseRepository mottattHendelseRepository;

    @Inject
    public AdminRestTjeneste(SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste,
                             OppgaveTjeneste oppgaveTjeneste, MottattHendelseRepository mottattHendelseRepository) {
        this.synkroniseringHendelseTaskOppretterTjeneste = synkroniseringHendelseTaskOppretterTjeneste;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.mottattHendelseRepository = mottattHendelseRepository;
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
        var behandlinger = behandlingIdListe.stream().map(EnkelBehandlingIdDto::getBehandlingId).collect(toList());
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTask(behandlinger);
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
        oppgaveTjeneste.adminAvsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlinger);
        return Response.ok().build();
    }

    @POST
    @Path("/resynk-for-retting")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter tilfelle før feilsituasjon", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response synkFørRetting() {
        var hendelser = mottattHendelseRepository.hentTilfelleMedFeilUUID();
        var sakHendelser = hendelser.stream()
                .filter(h -> h.getHendelseUid().startsWith(Kildesystem.FPSAK.name()))
                .map(h -> new BehandlingId(UUID.fromString(h.getHendelseUid().substring(5))))
                .filter(h -> oppgaveTjeneste.harOppgave(h))
                .map(h -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPSAK, h))
                .collect(toList());
        var tbkHendelser = hendelser.stream()
                .filter(h -> h.getHendelseUid().startsWith(Kildesystem.FPTILBAKE.name()))
                .map(h -> new BehandlingId(UUID.fromString(h.getHendelseUid().substring(9))))
                .filter(h -> oppgaveTjeneste.harOppgave(h))
                .map(h -> new SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId(Kildesystem.FPTILBAKE, h))
                .collect(toList());
        List<SynkroniseringHendelseTaskOppretterTjeneste.KildeBehandlingId> liste = new ArrayList<>(sakHendelser);
        liste.addAll(tbkHendelser);
        synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTasks(liste);
        return Response.ok().build();
    }

    @POST
    @Path("/slett-hendelse-pre-retting")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Sletter tilfelle før feilsituasjon", tags = "admin")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.DRIFT)
    public Response slettFørRetting() {
        mottattHendelseRepository.slettFørFeilTID();
        return Response.ok().build();
    }

}
