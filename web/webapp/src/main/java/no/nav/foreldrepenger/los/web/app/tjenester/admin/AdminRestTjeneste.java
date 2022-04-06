package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import static java.util.stream.Collectors.toList;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import java.util.List;

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
import no.nav.foreldrepenger.los.admin.OppgaveSynkroniseringTaskOppretterTjeneste;
import no.nav.foreldrepenger.los.admin.SynkroniseringHendelseTaskOppretterTjeneste;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.EnkelBehandlingIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.OppgaveKriterieTypeDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/admin")
@ApplicationScoped
@Transactional
public class AdminRestTjeneste {

    private OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste;
    private SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste;

    @Inject
    public AdminRestTjeneste(OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste,
                             SynkroniseringHendelseTaskOppretterTjeneste synkroniseringHendelseTaskOppretterTjeneste) {
        this.synkroniseringTjeneste = synkroniseringTjeneste;
        this.synkroniseringHendelseTaskOppretterTjeneste = synkroniseringHendelseTaskOppretterTjeneste;
    }

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @POST
    @Path("/synkroniser-egenskap")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Synkroniserer spesifisert oppgaveegenskap/kriterietype for åpne oppgaver", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response synkroniserBerørtBehandling(@NotNull @Valid OppgaveKriterieTypeDto oppgaveKriterieTypeDto) {
        var antallTasker = synkroniseringTjeneste.opprettOppgaveEgenskapOppdatererTask(oppgaveKriterieTypeDto.oppgaveEgenskap());
        return Response.ok(antallTasker).build();
    }

    @POST
    @Path("/synkroniser-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Oppretter task for synkronisering av behandling med fpsak", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    public Response synkroniserHendelser(@NotNull @Valid List<EnkelBehandlingIdDto> behandlingIdListe) {
        var behandlinger = behandlingIdListe.stream().map(EnkelBehandlingIdDto::getBehandlingId).collect(toList());
        var opprettedeTasker = synkroniseringHendelseTaskOppretterTjeneste.opprettOppgaveEgenskapOppdatererTask(behandlinger);
        return Response.ok(opprettedeTasker).build();
    }

}
