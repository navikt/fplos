package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdeling;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForAvdelingPerDato;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdag;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.OppgaverForFørsteStønadsdagUkeMåned;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("/avdelingsleder/nøkkeltall")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Transactional
public class NøkkeltallRestTjeneste {

    private NøkkeltallRepository nøkkeltallRepository;

    public NøkkeltallRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public NøkkeltallRestTjeneste(NøkkeltallRepository nøkkeltallRepository) {
        this.nøkkeltallRepository = nøkkeltallRepository;
    }

    @GET
    @Path("/behandlinger-under-arbeid")
    @Operation(description = "UnderArbeid", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForAvdeling> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentAlleOppgaverForAvdeling(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-under-arbeid-historikk")
    @Operation(description = "UA Historikk", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForAvdelingPerDato> getAntallOppgaverForAvdelingPerDato(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentAlleOppgaverForAvdelingPerDato(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-første-stønadsdag")
    @Operation(description = "Første stønadsdag", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForFørsteStønadsdag> getOppgaverPerFørsteStønadsdag(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentOppgaverPerFørsteStønadsdag(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-første-stønadsdag-uke")
    @Operation(description = "Første stønadsdag pr uke", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForFørsteStønadsdagUkeMåned> getOppgaverPerFørsteStønadsdagUke(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagUke(avdelingEnhet.getAvdelingEnhet());
    }

    @GET
    @Path("/behandlinger-første-stønadsdag-mnd")
    @Operation(description = "Første stønadsdag pr måned", tags = "AvdelingslederTall")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING_AVDELINGENHET, sporingslogg = false)
    public List<OppgaverForFørsteStønadsdagUkeMåned> getOppgaverPerFørsteStønadsdagMåned(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallRepository.hentOppgaverPerFørsteStønadsdagMåned(avdelingEnhet.getAvdelingEnhet());
    }
}
