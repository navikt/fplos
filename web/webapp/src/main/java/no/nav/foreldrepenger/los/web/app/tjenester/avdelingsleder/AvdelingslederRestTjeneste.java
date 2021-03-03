package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

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
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.OPPGAVESTYRING, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<AvdelingDto> hentAvdelinger() {
        return avdelingslederTjeneste.hentAvdelinger()
                .stream()
                .map(avdeling -> new AvdelingDto(avdeling.getId(), avdeling.getAvdelingEnhet(), avdeling.getNavn(), avdeling.getKreverKode6()))
                .collect(Collectors.toList());
    }
}
