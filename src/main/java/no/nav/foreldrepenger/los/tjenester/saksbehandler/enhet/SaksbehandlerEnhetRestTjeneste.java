package no.nav.foreldrepenger.los.tjenester.saksbehandler.enhet;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path("saksbehandler/enhet")
@ApplicationScoped
@Transactional
public class SaksbehandlerEnhetRestTjeneste {

    private OrganisasjonRepository organisasjonRepository;

    public SaksbehandlerEnhetRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public SaksbehandlerEnhetRestTjeneste(OrganisasjonRepository organisasjonRepository) {
        this.organisasjonRepository = organisasjonRepository;
    }

    @GET
    @Produces("application/json")
    @Operation(description = "Hent informasjon om tilhørende enhet for saksbehandler")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public List<SaksbehandlerEnhetDto> hentTilhørendeEnhet(@NotNull @Valid @QueryParam("ident") SaksbehandlerBrukerIdentDto saksbehandlerBrukerIdentDto) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(saksbehandlerBrukerIdentDto.getVerdi())
            .stream()
            .flatMap(sbh -> sbh.getAvdelinger().stream())
            .map(avdeling -> new SaksbehandlerEnhetDto(avdeling.getAvdelingEnhet(), avdeling.getNavn()))
            .toList();
    }
}
