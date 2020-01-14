package no.nav.foreldrepenger.los.web.app.tjenester.fagsak;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Path("/fagsak")
@RequestScoped
@Transactional
public class FagsakRestTjeneste {

    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste;

    public FagsakRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public FagsakRestTjeneste(FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste) {
        this.fagsakApplikasjonTjeneste = fagsakApplikasjonTjeneste;
    }

    @POST
    @Path("/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Søk etter saker på saksnummer eller fødselsnummer", tags = "Fagsaker")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<FagsakDto> søkFagsaker(@Parameter(description = "Søkestreng kan være saksnummer, fødselsnummer eller D-nummer.") @Valid SokefeltDto søkestreng) {
        return fagsakApplikasjonTjeneste.hentSaker(søkestreng.getSearchString());
    }
}
