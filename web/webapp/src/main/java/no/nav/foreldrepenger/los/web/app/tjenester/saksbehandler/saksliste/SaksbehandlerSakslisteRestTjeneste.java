package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "Saksbehandler")
@Path("/saksbehandler/saksliste")
@RequestScoped
@Transaction
public class SaksbehandlerSakslisteRestTjeneste {

    private OppgaveTjeneste oppgaveTjeneste;

    @Inject
    public SaksbehandlerSakslisteRestTjeneste(OppgaveTjeneste oppgaveTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    public SaksbehandlerSakslisteRestTjeneste() {
        // For Rest-CDI
    }

    @GET
    @Timed
    @Produces("application/json")
    @ApiOperation(value = "Henter sakslister")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SakslisteDto> hentSakslister() {
        List<OppgaveFiltrering> oppgaveFiltrerings = oppgaveTjeneste.hentOppgaveFiltreringerForPåloggetBruker();
        return oppgaveFiltrerings.stream().map(o -> new SakslisteDto(o)).collect(Collectors.toList());
    }

    @GET
    @Path("/saksbehandlere")
    @Timed
    @Produces("application/json")
    @ApiOperation(value = "Henter saksbehandlere tilknyttet en saksliste")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SaksbehandlerDto> hentSakslistensSaksbehandlere(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return oppgaveTjeneste.hentSakslistensSaksbehandlere(sakslisteId.getVerdi())
                .stream()
                .map(saksbehandler -> new SaksbehandlerDto(new SaksbehandlerBrukerIdentDto(saksbehandler.getSaksbehandlerIdent()), saksbehandler.getNavn(), saksbehandler.getAvdelinger()))
                .collect(Collectors.toList());
    }
}
