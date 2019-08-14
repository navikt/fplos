package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING;
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
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

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
                .map(saksbehandler -> lagSaksbehandlerDto(saksbehandler))
                .collect(Collectors.toList());
    }

    private SaksbehandlerDto lagSaksbehandlerDto(Saksbehandler saksbehandler) {
        String saksbehandlerIdent = saksbehandler.getSaksbehandlerIdent();
        List<String> organisasjonsEnhetsNavn = avdelingslederSaksbehandlerTjeneste.hentSaksbehandlersAvdelinger(saksbehandlerIdent.toUpperCase())
                .stream().map(org -> org.getEnhetNavn()).collect(Collectors.toList());
        return new SaksbehandlerDto(new SaksbehandlerBrukerIdentDto(saksbehandlerIdent),
                avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(saksbehandlerIdent.toUpperCase()),
                organisasjonsEnhetsNavn);
    }

    @POST
    @Timed
    @Path("/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Søk etter saksbehandlere", notes = (""))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @ApiParam("Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        String ident = brukerIdent.getVerdi().toUpperCase();
        if(avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(ident) != null) {
            List<String> organisasjonsEnhetsNavn = avdelingslederSaksbehandlerTjeneste.hentSaksbehandlersAvdelinger(ident)
                    .stream().map(org -> org.getEnhetNavn()).collect(Collectors.toList());
            return new SaksbehandlerDto(brukerIdent, avdelingslederSaksbehandlerTjeneste.hentSaksbehandlerNavn(ident), organisasjonsEnhetsNavn);
        }
        return null;
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
        avdelingslederSaksbehandlerTjeneste.leggTilSaksbehandler(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(),saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
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
}
