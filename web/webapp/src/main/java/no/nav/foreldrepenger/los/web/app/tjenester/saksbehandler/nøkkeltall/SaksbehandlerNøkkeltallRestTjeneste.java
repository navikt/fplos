package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall.dto.NyeOgFerdigstilteOppgaverDto;
import no.nav.fplos.statistikk.StatistikkTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = { "Saksbehandler" })
@Path("/saksbehandler/nokkeltall")
@RequestScoped
@Transaction
public class SaksbehandlerNøkkeltallRestTjeneste {

    private StatistikkTjeneste statistikkTjeneste;

    public SaksbehandlerNøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public SaksbehandlerNøkkeltallRestTjeneste(StatistikkTjeneste statistikkTjeneste) {
        this.statistikkTjeneste = statistikkTjeneste;
    }

    @GET
    @Timed
    @Path("/nye-og-ferdigstilte-oppgaver")
    @Produces("application/json")
    @ApiOperation(value = "Henter en oversikt over hvor mange oppgaver som er opprettet og ferdigstilt de siste syv dagene")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<NyeOgFerdigstilteOppgaverDto> getNyeOgFerdigstilteOppgaver(@NotNull @QueryParam("sakslisteId") @Valid SakslisteIdDto sakslisteId) {
        return statistikkTjeneste.hentNyeOgFerdigstilteOppgaver(sakslisteId.getVerdi())
                .stream()
                .map(resultat -> new NyeOgFerdigstilteOppgaverDto(resultat))
                .collect(Collectors.toList());
    }
}
