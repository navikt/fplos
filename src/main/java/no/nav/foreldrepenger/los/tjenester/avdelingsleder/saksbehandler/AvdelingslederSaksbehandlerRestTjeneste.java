package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler;

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
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.SaksbehandlerOgGruppeDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeNavneEndringRequestDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlerGruppeSletteRequestDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.dto.SaksbehandlereOgSaksbehandlerGrupper;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerDtoTjeneste;
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
    public List<SaksbehandlerDto> hentAvdelingensSaksbehandlere(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        return avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetDto.getAvdelingEnhet())
            .stream()
            .map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandler)
            .toList();
    }

    @POST
    @Path("/søk")
    @Operation(description = "Søk etter saksbehandler", tags = "AvdelingslederSaksbehandlere")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerDto søkAvdelingensSaksbehandlere(@NotNull @Parameter(description = "Brukeridentifikasjon") @Valid SaksbehandlerBrukerIdentDto brukerIdent) {
        return saksbehandlerDtoTjeneste.saksbehandlerDto(brukerIdent.getVerdi()).orElse(null);
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

    @GET
    @Path("/grupper")
    @Operation(description = "Avdelingsliste saksbehandlere og grupper", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public SaksbehandlereOgSaksbehandlerGrupper hentSaksbehandlerGrupper(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhetDto) {
        var avdelingensSaksbehandlere = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetDto.getAvdelingEnhet())
            .stream()
            .map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandler)
            .toList();
        // Litt dobbeltarbeid her i overgangsfase - vi henter saksbehandlere og mapper som før i tillegg til å gjøre samme med gruppene
        var saksbehandlereGruppe =  avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlereOgGrupper(avdelingEnhetDto.getAvdelingEnhet())
            .stream().map(g -> new SaksbehandlerGruppeDto(g.getId(), g.getGruppeNavn(), g.getSaksbehandlere().stream().map(saksbehandlerDtoTjeneste::lagKjentOgUkjentSaksbehandler).toList())).toList();
        return new SaksbehandlereOgSaksbehandlerGrupper(avdelingensSaksbehandlere, saksbehandlereGruppe);
    }

    @POST
    @Path("/grupper/opprett-gruppe")
    @Operation(description = "Oppretter gruppe", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public SaksbehandlerGruppeDto opprettSaksbehandlerGruppe(@Valid AvdelingEnhetDto dto) {
        var sbg = avdelingslederSaksbehandlerTjeneste.opprettSaksbehandlerGruppe(dto.getAvdelingEnhet());
        return new SaksbehandlerGruppeDto(sbg.getId(), sbg.getGruppeNavn(), List.of());
    }

    @POST
    @Path("/grupper/endre-gruppenavn")
    @Operation(description = "Gir nytt navn til gruppe", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.UPDATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void endreSaksbehandlerGruppe(@Valid SaksbehandlerGruppeNavneEndringRequestDto dto) {
        avdelingslederSaksbehandlerTjeneste.endreSaksbehandlerGruppeNavn(dto.gruppeId(), dto.gruppeNavn(), dto.avdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Path("/grupper/legg-til-saksbehandler")
    @Operation(description = "Legger saksbehandler til gruppe", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void leggSaksbehandlerTilGruppe(@Valid SaksbehandlerOgGruppeDto dto) {
        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilGruppe(dto.brukerIdent().getVerdi(), dto.gruppeId(), dto.avdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Path("/grupper/fjern-saksbehandler")
    @Operation(description = "Fjerner saksbehandler fra gruppe", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.CREATE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void fjernSaksbehandlerFraGruppe(@Valid SaksbehandlerOgGruppeDto dto) {
        avdelingslederSaksbehandlerTjeneste.fjernSaksbehandlerFraGruppe(dto.brukerIdent().getVerdi(), dto.gruppeId(), dto.avdelingEnhet().getAvdelingEnhet());
    }

    @POST
    @Path("/grupper/slett-saksbehandlergruppe")
    @Operation(description = "Sletter saksbehandlergruppe", tags = "AvdelingslederSaksbehandlergrupper")
    @BeskyttetRessurs(actionType = ActionType.DELETE, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET)
    public void slettSaksbehandlerGruppe(@Valid SaksbehandlerGruppeSletteRequestDto dto) {
        avdelingslederSaksbehandlerTjeneste.slettSaksbehandlerGruppe(dto.gruppeId(), dto.avdelingEnhet().getAvdelingEnhet());
    }
}
