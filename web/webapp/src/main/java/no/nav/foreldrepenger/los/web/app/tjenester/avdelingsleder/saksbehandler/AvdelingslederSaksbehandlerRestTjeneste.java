package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.stream.Collectors;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("avdelingsleder/saksbehandlere")
@ApplicationScoped
@Transactional
public class AvdelingslederSaksbehandlerRestTjeneste {

    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    private SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste;

    public AvdelingslederSaksbehandlerRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederSaksbehandlerRestTjeneste(AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste,
                                                   SaksbehandlerDtoTjeneste saksbehandlerDtoTjeneste) {
        this.avdelingslederSaksbehandlerTjeneste = avdelingslederSaksbehandlerTjeneste;
        this.saksbehandlerDtoTjeneste = saksbehandlerDtoTjeneste;
    }

    @GET
    @Produces("application/json")
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<SaksbehandlerMedAvdelingerDto> hentAvdelingensSaksbehandlere(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetDto.getAvdelingEnhet())
                .stream()
                .map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandlerMedAvdelingerDto)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/søk")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerMedAvdelingerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        return saksbehandlerDtoTjeneste.lagSaksbehandlerMedAvdelingerDto(brukerIdent.getVerdi())
                .orElse(null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Legg til ny saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void leggTilNySaksbehandler(
            @NotNull @Parameter(description = "Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(),
                saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Path("/slett")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fjern saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.CREATE, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void slettSaksbehandler( @NotNull @Parameter(description = "Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(), saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }
}
