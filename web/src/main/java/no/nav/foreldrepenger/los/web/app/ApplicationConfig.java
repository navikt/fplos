package no.nav.foreldrepenger.los.web.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.los.web.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.AdminRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.DriftsmeldingerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.NøkkeltallÅpneBehandlingerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave.AvdelingslederOppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner.AvdelingReservasjonerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.KodeverkRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall.SaksbehandlerNøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste.SaksbehandlerSakslisteRestTjeneste;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

//@ApplicationPath(ApplicationConfig.API_URI)
// TODO: ApplicationPath konfiges midlertidig via web.xml pga behov for to paths (/fplos/api og /api) ifb med overgang contextpath fra /fplos til /
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    public ApplicationConfig() {

        var oas = new OpenAPI();
        var info = new Info()
                .title("FPLOS")
                .version("1.0")
                .description("REST grensesnitt for fplos.");
        oas.info(info)
                .addServersItem(new Server()
                        .url("/fplos"));
        var oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                .resourcePackages(Stream.of("no.nav")
                        .collect(Collectors.toSet()));
        try {
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(KodeverkRestTjeneste.class,
                DriftsmeldingerRestTjeneste.class,
                SaksbehandlerSakslisteRestTjeneste.class,
                OppgaveRestTjeneste.class,
                AvdelingslederSakslisteRestTjeneste.class,
                AvdelingslederSaksbehandlerRestTjeneste.class,
                AvdelingReservasjonerRestTjeneste.class,
                NøkkeltallRestTjeneste.class,
                NøkkeltallÅpneBehandlingerRestTjeneste.class,
                AvdelingslederRestTjeneste.class,
                AvdelingslederOppgaveRestTjeneste.class,
                AdminRestTjeneste.class,
                SaksbehandlerNøkkeltallRestTjeneste.class,
                ProsessTaskRestTjeneste.class,
                OpenApiResource.class,
                JacksonJsonConfig.class,
                GeneralRestExceptionMapper.class,
                // ExceptionMappers pga de som finnes i Jackson+Jersey-media
                ConstraintViolationMapper.class,
                JsonMappingExceptionMapper.class,
                JsonParseExceptionMapper.class);
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
