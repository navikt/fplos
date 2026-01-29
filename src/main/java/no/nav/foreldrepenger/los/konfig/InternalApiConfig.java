package no.nav.foreldrepenger.los.konfig;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ApplicationPath;
import no.nav.foreldrepenger.los.server.PrometheusRestService;
import no.nav.foreldrepenger.los.server.healthcheck.HealthCheckRestService;

@ApplicationPath(InternalApiConfig.API_URI)
public class InternalApiConfig extends ResourceConfig {

    public static final String API_URI = "/internal";

    public InternalApiConfig() {
        register(HealthCheckRestService.class);
        register(PrometheusRestService.class);
    }
}
