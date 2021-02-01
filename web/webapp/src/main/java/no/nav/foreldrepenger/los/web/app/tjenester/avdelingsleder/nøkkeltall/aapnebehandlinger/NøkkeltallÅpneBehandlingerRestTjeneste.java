package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.aapnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/avdelingsleder/nokkeltall")
@ApplicationScoped
@Transactional
public class NøkkeltallÅpneBehandlingerRestTjeneste {

    private NøkkeltallBehandlingerVentestatus nøkkeltallBehandlingerVentestatus;

    public NøkkeltallÅpneBehandlingerRestTjeneste() {
    }

    @Inject
    public NøkkeltallÅpneBehandlingerRestTjeneste(NøkkeltallBehandlingerVentestatus statistikkTjeneste) {
        this.nøkkeltallBehandlingerVentestatus = statistikkTjeneste;
    }

    @GET
    @Path("/aapne-behandlinger")
    @Produces("application/json")
    @Operation(description = "AapneBehandlinger", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<NøkkeltallBehandlingVentestatusDto> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallBehandlingerVentestatus.hentBehandlingVentestatusNøkkeltall(avdelingEnhet.getAvdelingEnhet());
    }

}
