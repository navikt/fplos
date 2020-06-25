package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/saksbehandler/saksliste")
@ApplicationScoped
@Transactional
public class SaksbehandlerSakslisteRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    @Inject
    public SaksbehandlerSakslisteRestTjeneste(OppgaveTjeneste oppgaveTjeneste,
                                              SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    public SaksbehandlerSakslisteRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Produces("application/json")
    @Operation(description = "Henter sakslister", tags = "Saksliste")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SakslisteDto> hentSakslister() {
        List<OppgaveFiltrering> filtre = oppgaveTjeneste.hentOppgaveFiltreringerForPåloggetBruker();
        return filtre.stream()
                .map(o -> new SakslisteDto(o, oppgaveTjeneste.hentAntallOppgaver(o.getId(), false)))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/saksbehandlere")
    @Produces("application/json")
    @Operation(description = "Henter saksbehandlere tilknyttet en saksliste", tags = "Saksliste")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SaksbehandlerDto> hentSakslistensAktiveSaksbehandlere(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return saksbehandlerDtoTjeneste.hentAktiveSaksbehandlereTilknyttetSaksliste(sakslisteId.getVerdi());
    }
}
