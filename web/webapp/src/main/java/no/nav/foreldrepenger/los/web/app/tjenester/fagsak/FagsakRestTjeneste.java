package no.nav.foreldrepenger.los.web.app.tjenester.fagsak;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;

@Api(tags = { "Fagsaker" })
@Path("/fagsak")
@RequestScoped
@Transaction
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
    @Timed
    @Path("/sok")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Søk etter saker på saksnummer eller fødselsnummer", notes = ("Spesifikke saker kan søkes via saksnummer. " +
        "Oversikt over saker knyttet til en bruker kan søkes via fødselsnummer eller d-nummer."))
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, ressurs = BeskyttetRessursResourceAttributt.FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<FagsakDto> søkFagsaker(@ApiParam("Søkestreng kan være saksnummer, fødselsnummer eller D-nummer.") @Valid SokefeltDto søkestreng) {
        return fagsakApplikasjonTjeneste.hentSaker(søkestreng.getSearchString());
    }
}
