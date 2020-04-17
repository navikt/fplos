package no.nav.foreldrepenger.los.web.app.tjenester.konfig;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.APPLIKASJON;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path("/konfig")
@ApplicationScoped
public class KonfigRestTjeneste {

    private String fpsakFrontendUrl;

    private String fptilbakeFrontendUrl;

    public KonfigRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public KonfigRestTjeneste(@KonfigVerdi("fpsak.frontend.url") String fpsakFrontendUrl, @KonfigVerdi("fptilbake.frontend.url") String fptilbakeFrontendUrl) {
        this.fpsakFrontendUrl = fpsakFrontendUrl;
        this.fptilbakeFrontendUrl = fptilbakeFrontendUrl;
    }

    @GET
    @Path("/fpsak-url")
    @Produces("application/json")
    @Operation(description = "Henter basis lenke til FPSAK.", tags = "Konfigurasjon")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Konfig hentFpsakUrl() {
        return new Konfig(fpsakFrontendUrl);
    }

    @GET
    @Path("/fptilbake-url")
    @Produces("application/json")
    @Operation(description = "Henter basis lenke til FPTILBAKE.", tags = "Konfigurasjon")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Konfig hentFptilbakeUrl() {
        return new Konfig(fptilbakeFrontendUrl);
    }

    public static class Konfig {

        private String verdi;

        public Konfig(String verdi) {
            this.verdi = verdi;
        }

        public String getVerdi() {
            return verdi;
        }
    }

}
