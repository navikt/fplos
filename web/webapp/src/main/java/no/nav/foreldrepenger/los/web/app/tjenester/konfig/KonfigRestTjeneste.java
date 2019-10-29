package no.nav.foreldrepenger.los.web.app.tjenester.konfig;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.APPLIKASJON;

@Api(tags = "Konfigurasjon")
@Path("/konfig")
@RequestScoped
@Transaction
public class KonfigRestTjeneste {

    private String fpsakFrontendUrl;
    private String fptilbakeFrontendUrl;

    public KonfigRestTjeneste() {
        //NOSONAR
    }

    @Inject
    public KonfigRestTjeneste(@KonfigVerdi("fpsak.frontend.url") String fpsakFrontendUrl,
                              @KonfigVerdi("fptilbake.frontend.url") String fptilbakeFrontendUrl) {
        this.fpsakFrontendUrl = fpsakFrontendUrl;
        this.fptilbakeFrontendUrl = fptilbakeFrontendUrl;
    }

    @GET
    @Timed
    @Path("/fpsak-url")
    @Produces("application/json")
    @ApiOperation(value = "Henter basis lenke til FPSAK.")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Konfig hentFpsakUrl() {
        return new Konfig(fpsakFrontendUrl);
    }

    @GET
    @Timed
    @Path("/fptilbake-url")
    @Produces("application/json")
    @ApiOperation(value = "Henter basis lenke til FPTILBAKE.")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Konfig hentFptilbakeUrl() {
        return new Konfig(fptilbakeFrontendUrl);
    }

    @GET
    @Timed
    @Path("/feature-toggles")
    @Produces("application/json")
    @ApiOperation(value = "Henter alle feature toggles som skal brukes i klient.")
    @BeskyttetRessurs(action = READ, ressurs = APPLIKASJON, sporingslogg = false)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public Map<String, Boolean> getFeatureToggles() {
        //FIXME Hent fra Unleash
        Map<String, Boolean> map = new HashMap<>();
        return map;
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
