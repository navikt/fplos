package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerMedAvdelingerDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
    @Operation(description = "Henter alle saksbehandlere", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<SaksbehandlerMedAvdelingerDto> hentAvdelingensSaksbehandlere(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetDto.getAvdelingEnhet())
            .stream()
            .map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandlerMedAvdelingerDto)
            .toList();
    }

    @POST
    @Path("/søk")
    @Operation(description = "Søk etter saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerMedAvdelingerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        return saksbehandlerDtoTjeneste.lagSaksbehandlerMedAvdelingerDto(brukerIdent.getVerdi()).orElse(null);
    }

    @POST
    @Operation(description = "Legg til ny saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void leggTilNySaksbehandler(@NotNull @Parameter(description = "Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(),
            saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Path("/slett")
    @Operation(description = "Fjern saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void slettSaksbehandler(@NotNull @Parameter(description = "Brukeridentifikasjon og avdelingsid") @Valid SaksbehandlerOgAvdelingDto saksbehandlerOgAvdeling) {
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraAvdeling(saksbehandlerOgAvdeling.getBrukerIdent().getVerdi(),
            saksbehandlerOgAvdeling.getAvdelingEnhet().getAvdelingEnhet());
    }
}
