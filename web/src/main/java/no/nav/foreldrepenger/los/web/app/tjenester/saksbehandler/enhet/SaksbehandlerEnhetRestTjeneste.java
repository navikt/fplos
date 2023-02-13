package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.enhet;

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
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
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
        var saksbehandler = organisasjonRepository.hentSaksbehandler(saksbehandlerBrukerIdentDto.getVerdi());
        if (saksbehandler == null || saksbehandler.getAvdelinger() == null) {
            return List.of();
        }
        return saksbehandler.getAvdelinger().stream()
                .map(avdeling -> new SaksbehandlerEnhetDto(avdeling.getAvdelingEnhet(), avdeling.getNavn()))
                .toList();
    }
}