package no.nav.foreldrepenger.los.web.app.konfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.prometheus.client.hotspot.DefaultExports;
import no.nav.foreldrepenger.los.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.los.web.app.tjenester.NaisRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.SelftestRestTjeneste;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    public static final String API_URL = "/internal";

    public InternalApplication() {
        // CDI
        DefaultExports.initialize();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(NaisRestTjeneste.class);
        classes.add(SelftestRestTjeneste.class);
        classes.add(PrometheusRestService.class);

        return Collections.unmodifiableSet(classes);
    }
}
