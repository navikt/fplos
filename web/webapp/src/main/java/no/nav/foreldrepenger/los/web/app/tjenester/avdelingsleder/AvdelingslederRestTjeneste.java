package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.OPPGAVESTYRING;

@Api(tags = "Avdelingsleder")
@Path("/avdelingsleder")
@RequestScoped
@Transaction
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
    @Timed
    @Path("/avdelinger")
    @Produces("application/json")
    @ApiOperation(value = "Henter alle avdelinger")
    @BeskyttetRessurs(action = READ, ressurs = OPPGAVESTYRING, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<AvdelingDto> hentAvdelinger() {
        return avdelingslederTjeneste.hentAvdelinger()
                .stream()
                .map(avdeling -> new AvdelingDto(avdeling.getId(), avdeling.getAvdelingEnhet(), avdeling.getNavn(), avdeling.getKreverKode6()))
                .collect(Collectors.toList());
    }
}
