package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

import no.nav.foreldrepenger.los.web.app.util.StringUtils;

@Api(tags = "Avdelingsleder")
@Path("avdelingsleder/saksbehandlere")
@RequestScoped
@Transaction
public class AvdelingslederSaksbehandlerRestTjeneste {

    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;

    public AvdelingslederSaksbehandlerRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederSaksbehandlerRestTjeneste(AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste) {
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
    }

    @GET
    @Timed
    @Produces("application/json")
    @ApiOperation(value = "Henter alle saksbehandlere")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SaksbehandlerDto> hentAvdelingensSaksbehandlere(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetDto.getAvdelingEnhet())
                .stream()
                .map(this::saksbehandlerDtoFra)
                .collect(Collectors.toList());
    }

    @POST
    @Timed
    @Path("/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Søk etter saksbehandler", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @ApiParam("Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        return saksbehandlerDtoFra(brukerIdent);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Legg til ny saksbehandler", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void leggTilNySaksbehandler(
            @NotNull @ApiParam("Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(),
                saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Timed
    @Path("/slett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Fjern saksbehandler", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void slettSaksbehandler( @NotNull @ApiParam("Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.slettSaksbehandler(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(), saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }

    private SaksbehandlerDto saksbehandlerDtoFra(Saksbehandler saksbehandler) {
        return saksbehandlerDtoFra(new SaksbehandlerBrukerIdentDto(saksbehandler.getSaksbehandlerIdent().toUpperCase()));
    }

    private SaksbehandlerDto saksbehandlerDtoFra(SaksbehandlerBrukerIdentDto brukerIdent) {
        return new SaksbehandlerDto(brukerIdent, hentSaksbehandlersNavn(brukerIdent), tilgjengeligeEnheterFor(brukerIdent));
    }

    private String hentSaksbehandlersNavn(SaksbehandlerBrukerIdentDto brukerIdentDto) {
        return avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(brukerIdentDto.getVerdi().toUpperCase());
    }

    private List<String> tilgjengeligeEnheterFor(SaksbehandlerBrukerIdentDto brukerIdent) {
        return avdelingslederSaksbehandlerTjeneste.hentSaksbehandlersAvdelinger(brukerIdent.getVerdi().toUpperCase()).stream()
                .map(OrganisasjonsEnhet::getEnhetNavn)
                .collect(Collectors.toList());
    }
}
