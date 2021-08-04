package no.nav.foreldrepenger.los.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.hotspot.DefaultExports;
import no.nav.foreldrepenger.los.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.los.web.app.tjenester.HealthCheckRestService;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(InternalApplication.class);

    public static final String API_URL = "/internal";

    public InternalApplication() {
        // CDI
        try {
            DefaultExports.initialize();
        } catch (Exception e) {
            LOG.error("LOS startup exception fra defaultexports", e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(HealthCheckRestService.class, PrometheusRestService.class);
    }
}
