package no.nav.foreldrepenger.los.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.prometheus.client.hotspot.DefaultExports;
import no.nav.foreldrepenger.los.web.app.healthcheck.HealthCheckRestService;
import no.nav.foreldrepenger.los.web.app.metrics.PrometheusRestService;

@ApplicationPath(InternalApiConfig.API_URL)
public class InternalApiConfig extends Application {

    public static final String API_URL = "/internal";

    public InternalApiConfig() {
        // CDI
        DefaultExports.initialize();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(HealthCheckRestService.class, PrometheusRestService.class);
    }
}
