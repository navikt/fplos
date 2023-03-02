package no.nav.foreldrepenger.los.web.app.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.enhet.SaksbehandlerEnhetRestTjeneste;

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
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

@ApplicationPath(ApiConfig.API_URI)
public class ApiConfig extends Application {

    private static final Environment ENV = Environment.current();

    static final String API_URI = "/api";

    public ApiConfig() {

        var oas = new OpenAPI();
        var info = new Info()
                .title("FPLOS")
                .version("1.0")
                .description("REST grensesnitt for fplos.");
        oas.info(info)
                .addServersItem(new Server()
                        .url(ENV.getProperty("context.path", "/fplos")));
        var oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourceClasses(ApiConfig.getAllClasses().stream().map(Class::getName).collect(Collectors.toSet()));
        try {
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new TekniskException("OPENAPI", e.getMessage(), e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        // eksponert grensesnitt
        Set<Class<?>> classes = new HashSet<>(getAllClasses());

        // swagger
        classes.add(OpenApiResource.class);

        // Applikasjonsoppsett
        classes.add(JacksonJsonConfig.class);

        // ExceptionMappers pga de som finnes i Jackson+Jersey-media
        classes.add(ConstraintViolationMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(JsonParseExceptionMapper.class);

        // Generell exceptionmapper m/logging for øvrige tilfelle
        classes.add(GeneralRestExceptionMapper.class);

        return Collections.unmodifiableSet(classes);
    }

    private static Collection<Class<?>> getAllClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(KodeverkRestTjeneste.class);
        classes.add(DriftsmeldingerRestTjeneste.class);
        classes.add(SaksbehandlerSakslisteRestTjeneste.class);
        classes.add(OppgaveRestTjeneste.class);
        classes.add(AvdelingslederSakslisteRestTjeneste.class);
        classes.add(AvdelingslederSaksbehandlerRestTjeneste.class);
        classes.add(AvdelingReservasjonerRestTjeneste.class);
        classes.add(NøkkeltallRestTjeneste.class);
        classes.add(NøkkeltallÅpneBehandlingerRestTjeneste.class);
        classes.add(AvdelingslederRestTjeneste.class);
        classes.add(AvdelingslederOppgaveRestTjeneste.class);
        classes.add(AdminRestTjeneste.class);
        classes.add(SaksbehandlerEnhetRestTjeneste.class);
        classes.add(SaksbehandlerNøkkeltallRestTjeneste.class);
        classes.add(ProsessTaskRestTjeneste.class);
        return classes;
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
