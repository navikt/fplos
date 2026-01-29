package no.nav.foreldrepenger.los.konfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.konfig.Environment;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import no.nav.foreldrepenger.los.server.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.los.server.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.los.tjenester.admin.AdminRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.admin.DriftsmeldingerAdminRestTjeneste;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

import static no.nav.foreldrepenger.los.konfig.ApiConfig.getFellesConfigClasses;

@ApplicationPath(ForvaltningApiConfig.API_URI)
public class ForvaltningApiConfig extends ResourceConfig {
    public static final String API_URI = "/forvaltning/api";
    private static final Environment ENV = Environment.current();

    public ForvaltningApiConfig() {
        register(ForvaltningAuthorizationFilter.class); // Autorisering - drift
        registerClasses(getFellesConfigClasses());
        registerOpenApi();
        registerClasses(getForvaltningKlasser());
        setProperties(getApplicationProperties());
    }

    private void registerOpenApi() {
        var oas = new OpenAPI();
        var info = new Info().title(ENV.getNaisAppName())
            .version(Optional.ofNullable(ENV.imageName()).orElse("1.0"))
            .description("REST grensesnitt for fplos.");

        oas.info(info).addServersItem(new Server().url(ENV.getProperty("context.path", "/fplos")));
        var oasConfig = new SwaggerConfiguration().openAPI(oas)
            .prettyPrint(true)
            .resourceClasses(getForvaltningKlasser().stream().map(Class::getName).collect(Collectors.toSet()));
        try {
            new GenericOpenApiContextBuilder<>().openApiConfiguration(oasConfig).buildContext(true).read();
        } catch (OpenApiConfigurationException e) {
            throw new TekniskException("OPEN-API", e.getMessage(), e);
        }

        register(OpenApiResource.class);
    }

    private static Set<Class<?>> getForvaltningKlasser() {
        return Set.of(DriftsmeldingerAdminRestTjeneste.class, AdminRestTjeneste.class, ProsessTaskRestTjeneste.class);
    }

    private static Map<String, Object> getApplicationProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Ref Jersey doc
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        return properties;
    }

}
