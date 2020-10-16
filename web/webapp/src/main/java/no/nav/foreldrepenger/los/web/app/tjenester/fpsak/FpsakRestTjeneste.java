package no.nav.foreldrepenger.los.web.app.tjenester.fpsak;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;

import java.util.NoSuchElementException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/fpsak")
@ApplicationScoped
public class FpsakRestTjeneste {

    private ForeldrepengerBehandlingKlient foreldrepengerBehandlingKlient;

    @Inject
    public FpsakRestTjeneste(ForeldrepengerBehandlingKlient foreldrepengerBehandlingKlient) {
        this.foreldrepengerBehandlingKlient = foreldrepengerBehandlingKlient;
    }

    public FpsakRestTjeneste() {
    }

    @GET
    @Path("/behandlingId")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Henter fpsak intern behandlingId basert på ekstern behandlingsid", tags = "Fpsak")
    @BeskyttetRessurs(action = READ, resource = AbacAttributter.APPLIKASJON)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Long hentFpsakInternBehandlingId(@NotNull @Valid @QueryParam("uuid") BehandlingIdDto behandlingId) {
        return foreldrepengerBehandlingKlient.getFpsakInternBehandlingId(behandlingId.getValue())
                .orElseThrow(() -> new NoSuchElementException("Fant ingen intern behandloingId for ekstern id : " + behandlingId));
    }
}
