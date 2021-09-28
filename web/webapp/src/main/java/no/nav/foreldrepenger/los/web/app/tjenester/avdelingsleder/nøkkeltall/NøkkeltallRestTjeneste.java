package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall;

import java.util.List;

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
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdelingPerDato;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForAvdelingSattManueltPåVent;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.OppgaverForFørsteStønadsdag;
import no.nav.foreldrepenger.los.statistikk.statistikk_gammel.StatistikkTjeneste;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("/avdelingsleder/nøkkeltall")
@ApplicationScoped
@Transactional
public class NøkkeltallRestTjeneste {

    private StatistikkTjeneste statistikkTjeneste;

    public NøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public NøkkeltallRestTjeneste(StatistikkTjeneste statistikkTjeneste) {
        this.statistikkTjeneste = statistikkTjeneste;
    }

    @GET
    @Path("/behandlinger-under-arbeid")
    @Produces("application/json")
    @Operation(description = "UnderArbeid", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdeling> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAlleOppgaverForAvdeling(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-under-arbeid-historikk")
    @Produces("application/json")
    @Operation(description = "UA Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdelingPerDato> getAntallOppgaverForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-manuelt-vent-historikk")
    @Produces("application/json")
    @Operation(description = "ManueltVent Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdelingSattManueltPåVent> getAntallOppgaverSattPåManuellVentForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAntallOppgaverForAvdelingSattManueltPåVent(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-første-stønadsdag")
    @Produces("application/json")
    @Operation(description = "Første stønadsdag", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForFørsteStønadsdag> getOppgaverPerFørsteStønadsdag(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentOppgaverPerFørsteStønadsdag(avdelingEnhet.getAvdelingEnhet());
    }
}
