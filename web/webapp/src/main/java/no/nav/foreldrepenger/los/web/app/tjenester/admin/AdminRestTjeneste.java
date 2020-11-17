package no.nav.foreldrepenger.los.web.app.tjenester.admin;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.AvdelingOpprettelseDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.OppgaveEventLoggDto;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.dto.OppgaveKriterieTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.fplos.admin.AdminTjeneste;
import no.nav.fplos.admin.OppgaveSynkroniseringTaskOppretterTjeneste;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;

import javax.enterprise.context.ApplicationScoped;
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
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.CREATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

@Path("/admin")
@ApplicationScoped
@Transactional
public class AdminRestTjeneste {

    private AdminTjeneste adminTjeneste;
    private OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;

    @Inject
    public AdminRestTjeneste(AdminTjeneste adminTjeneste,
                             OppgaveSynkroniseringTaskOppretterTjeneste synkroniseringTjeneste,
                             OppgaveDtoTjeneste oppgaveDtoTjeneste) {
        this.adminTjeneste = adminTjeneste;
        this.synkroniseringTjeneste = synkroniseringTjeneste;
        this.oppgaveDtoTjeneste = oppgaveDtoTjeneste;
    }

    public AdminRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Path("/synkroniseroppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Synkroniser oppgave", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto synkroniserOppgave(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingId) {
        var oppgave = adminTjeneste.synkroniserOppgave(behandlingId.getValue());
        return map(oppgave);
    }

    @GET
    @Path("/hentoppgaver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Liste over oppgaver tilknyttet sak. Merk at tilbakekrevingsspesifikke detaljer ikke vises.", tags = "admin")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentOppgave(@TilpassetAbacAttributt(supplierClass = AbacDataSupplier.class) @NotNull @QueryParam("saksnummer") @Valid Saksnummer saksnummer) {
        // som for øvrige admintjenester er ABAC for oppgaveDto håndtert i oppgaveDtoTjeneste.
        return adminTjeneste.hentOppgaver(saksnummer).stream()
                .map(this::map)
                .collect(toList());
    }

    @GET
    @Path("/sepaaeventer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Se på oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveEventLoggDto> hentEventlogg(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingId) {
        var oppgaveEventLogger = adminTjeneste.hentEventer(behandlingId.getValue());
        return oppgaveEventLogger.stream()
                .map(OppgaveEventLoggDto::new)
                .collect(toList());
    }

    @GET
    @Path("/hent-alle-oppgaver-knyttet-til-behandling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter ut alle oppgaver knyttet til behandling", tags = "admin")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.OPPGAVESTYRING)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaveDto> hentAlleOppgaverForBehandling(@NotNull @QueryParam("behandlingId") @Valid BehandlingIdDto behandlingId) {
        List<Oppgave> oppgaver = adminTjeneste.hentAlleOppgaverForBehandling(behandlingId.getValue());
        return oppgaver.stream()
                .map(this::map)
                .collect(toList());
    }

    @GET
    @Path("/deaktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deaktiver oppgave", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto deaktiverOppgave(@NotNull @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveIdDto) {
        var oppgave = adminTjeneste.deaktiverOppgave(oppgaveIdDto.getVerdi());
        return map(oppgave);
    }

    @GET
    @Path("/aktiver-oppgave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Aktiver oppgave", tags = "admin")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public OppgaveDto aktiverOppgave(@NotNull @QueryParam("oppgaveId") @Valid OppgaveIdDto oppgaveIdDto) {
        var oppgave = adminTjeneste.aktiverOppgave(oppgaveIdDto.getVerdi());
        return map(oppgave);
    }

    @POST
    @Path("/synkroniser-egenskap")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Synkroniserer spesifisert oppgaveegenskap/kriterietype for åpne oppgaver", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response synkroniserBerørtBehandling(@NotNull @Valid OppgaveKriterieTypeDto oppgaveKriterieTypeDto) {
        var antallTasker = synkroniseringTjeneste.opprettOppgaveEgenskapOppdatererTask(oppgaveKriterieTypeDto.getVerdi());
        return Response.ok(antallTasker).build();
    }

    @POST
    @Path("/opprettAvdeling")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Opprett avdeling", tags = "admin")
    @BeskyttetRessurs(action = CREATE, resource = AbacAttributter.DRIFT)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Response opprettAvdeling(@NotNull @Valid AvdelingOpprettelseDto dto) {
        Avdeling nyAvdeling = new Avdeling(dto.getAvdelingKode(), dto.getAvdelingNavn(), dto.getKreverKode6Tilgang());
        adminTjeneste.opprettAvdeling(nyAvdeling);
        return Response.noContent().build();
    }

    private OppgaveDto map(Oppgave oppgave) {
        return oppgaveDtoTjeneste.lagDtoFor(oppgave, false);
    }

    public static class AbacDataSupplier implements Function<Object, AbacDataAttributter> {
        @Override
        public AbacDataAttributter apply(Object obj) {
            return AbacDataAttributter.opprett();
        }
    }
}
