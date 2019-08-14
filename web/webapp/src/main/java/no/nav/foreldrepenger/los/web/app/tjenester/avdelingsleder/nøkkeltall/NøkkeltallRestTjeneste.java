package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall;

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
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingPerDatoDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingSattManueltPaaVentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagDto;
import no.nav.fplos.statistikk.StatistikkTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Api(tags = { "Avdelingsleder" })
@Path("/avdelingsleder/nokkeltall")
@RequestScoped
@Transaction
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
    @Timed
    @Path("/behandlinger-under-arbeid")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdelingDto> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAlleOppgaverForAvdeling(avdelingEnhet.getAvdelingEnhet())
                .stream()
                .map(resultat -> new OppgaverForAvdelingDto(resultat))
                .collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("/behandlinger-under-arbeid-historikk")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdelingPerDatoDto> getAntallOppgaverForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAntallOppgaverForAvdelingPerDato(avdelingEnhet.getAvdelingEnhet())
                .stream()
                .map(resultat -> new OppgaverForAvdelingPerDatoDto(resultat))
                .collect(Collectors.toList());
    }


    @GET
    @Timed
    @Path("/behandlinger-manuelt-vent-historikk")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForAvdelingSattManueltPaaVentDto> getAntallOppgaverSattPåManuellVentForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentAntallOppgaverForAvdelingSattManueltPåVent(avdelingEnhet.getAvdelingEnhet())
                .stream()
                .map(result -> new OppgaverForAvdelingSattManueltPaaVentDto(result))
                .collect(Collectors.toList());
    }

    @GET
    @Timed
    @Path("/behandlinger-forste-stonadsdag")
    @Produces("application/json")
    @ApiOperation(value = "", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<OppgaverForFørsteStønadsdagDto> getOppgaverPerFørsteStønadsdag(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return statistikkTjeneste.hentOppgaverPerFørsteStønadsdag(avdelingEnhet.getAvdelingEnhet())
                .stream()
                .map(result -> new OppgaverForFørsteStønadsdagDto(result))
                .collect(Collectors.toList());
    }
}
