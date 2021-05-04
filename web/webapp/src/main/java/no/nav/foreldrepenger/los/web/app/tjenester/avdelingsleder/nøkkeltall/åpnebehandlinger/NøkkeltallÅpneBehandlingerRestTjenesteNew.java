package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

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
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@Path("/avdelingsleder/nøkkeltall")
@ApplicationScoped
@Transactional
public class NøkkeltallÅpneBehandlingerRestTjenesteNew {

    private NøkkeltallBehandlingerVentestatus nøkkeltallBehandlingerVentestatus;

    public NøkkeltallÅpneBehandlingerRestTjenesteNew() {
    }

    @Inject
    public NøkkeltallÅpneBehandlingerRestTjenesteNew(NøkkeltallBehandlingerVentestatus statistikkTjeneste) {
        this.nøkkeltallBehandlingerVentestatus = statistikkTjeneste;
    }

    @GET
    @Path("/åpne-behandlinger")
    @Produces("application/json")
    @Operation(description = "Åpne behandlinger", tags = "AvdelingslederTall")
    @BeskyttetRessurs(action = BeskyttetRessursActionAttributt.READ, resource = AbacAttributter.OPPGAVESTYRING_AVDELINGENHET)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public List<NøkkeltallBehandlingVentestatusDto> getAlleOppgaverForAvdeling(@NotNull @QueryParam("avdelingEnhet") @Valid AvdelingEnhetDto avdelingEnhet) {
        return nøkkeltallBehandlingerVentestatus.hentBehandlingVentestatusNøkkeltall(avdelingEnhet.getAvdelingEnhet());
    }

}
