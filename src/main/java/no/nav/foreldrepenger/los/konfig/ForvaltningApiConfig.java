package no.nav.foreldrepenger.los.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.models.info.Info;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.tjenester.admin.AdminRestTjeneste;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

@ApplicationPath(ForvaltningApiConfig.API_URL)
public class ForvaltningApiConfig extends Application {

    private static final Environment ENV = Environment.current();

    public static final String API_URL = "/forvaltning/api";

    public ForvaltningApiConfig() {
        registerOpenApi();
    }

    private void registerOpenApi() {
        var info = new Info()
            .title("FPLOS Forvaltning - Foreldrepenger, engangsstønad og svangerskapspenger")
            .version(Optional.ofNullable(ENV.imageName()).orElse("1.0"))
            .description("REST grensesnitt for FP-LOS.");
        var contextPath = ENV.getProperty("context.path", "/fplos");
        OpenApiUtils.openApiConfigFor(info, contextPath, this)
            .registerClasses(getAllClasses())
            .buildOpenApiContext();
    }

    @Override
    public Set<Class<?>> getClasses() {
        // eksponert grensesnitt
        Set<Class<?>> classes = new HashSet<>(getAllClasses());

        // Autentisering og autorisering
        classes.addAll(FellesConfigClasses.getFellesContainerFilterClasses());
        classes.add(ForvaltningAuthorizationFilter.class);

        // swagger
        classes.add(OpenApiResource.class);

        // Plugger inn våre komponenter
        classes.addAll(FellesConfigClasses.getFellesRsExtConfigClasses());

        return Collections.unmodifiableSet(classes);
    }

    private static Collection<Class<?>> getAllClasses() {
        return Set.of(AdminRestTjeneste.class, ProsessTaskRestTjeneste.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Ref Jersey doc
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        return properties;
    }

}
