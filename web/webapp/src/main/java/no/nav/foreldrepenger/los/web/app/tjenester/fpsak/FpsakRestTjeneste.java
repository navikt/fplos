package no.nav.foreldrepenger.los.web.app.tjenester.fpsak;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.APPLIKASJON;

@Path("/fpsak")
@RequestScoped
@Transactional
public class FpsakRestTjeneste {

    ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient;

    @Inject
    public FpsakRestTjeneste(ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient) {
        this.foreldrepengerBehandlingRestKlient = foreldrepengerBehandlingRestKlient;
    }

    public FpsakRestTjeneste() {
    }

    @GET
    @Path("/behandlingId")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter behandlingId basert på uuid", tags = "Fpsak")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Long hentBehandlingId(@NotNull @Valid @QueryParam("uuid") BehandlingIdDto behandlingIdDto) throws URISyntaxException {
        if(behandlingIdDto.getUuid() == null) throw new IllegalArgumentException("Ugyldig verdi for parameter 'uuid'");
        return foreldrepengerBehandlingRestKlient.getBehandlingIdFraUUID(behandlingIdDto.getUuid()).orElseThrow(() -> new NoSuchElementException("Fant ingen behandlingId for uuid : " + behandlingIdDto.getUuid()));
    }
}
