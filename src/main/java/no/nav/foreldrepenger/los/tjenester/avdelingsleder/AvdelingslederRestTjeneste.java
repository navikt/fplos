package no.nav.foreldrepenger.los.tjenester.avdelingsleder;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.util.Comparator;
import java.util.List;

@Path("/avdelingsleder")
@ApplicationScoped
@Transactional
public class AvdelingslederRestTjeneste {

    private AvdelingslederTjeneste avdelingslederTjeneste;

    public AvdelingslederRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public AvdelingslederRestTjeneste(AvdelingslederTjeneste avdelingslederTjeneste) {
        this.avdelingslederTjeneste = avdelingslederTjeneste;
    }

    @GET
    @Path("/avdelinger")
    @Produces("application/json")
    @Operation(description = "Henter alle avdelinger", tags = "AvdelingslederTopp")
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.OPPGAVESTYRING, sporingslogg = false)
    public List<AvdelingDto> hentAvdelinger() {
        return avdelingslederTjeneste.hentAvdelinger()
            .stream()
            .map(avdeling -> new AvdelingDto(avdeling.getId(), avdeling.getAvdelingEnhet(), avdeling.getNavn(), avdeling.getKreverKode6()))
            .sorted(Comparator.comparing(AvdelingDto::getKreverKode6).thenComparing(a -> Long.valueOf(a.getAvdelingEnhet())))
            .toList();
    }
}
