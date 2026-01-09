package no.nav.foreldrepenger.los.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.server.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.los.server.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.los.tjenester.admin.AdminRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.admin.DriftsmeldingerRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.oppgave.AvdelingslederOppgaveRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.reservasjon.AvdelingReservasjonerRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.kodeverk.KodeverkRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.migrering.MigreringRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.reservasjon.ReservasjonRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.saksliste.SaksbehandlerSakslisteRestTjeneste;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.prosesstask.rest.ProsessTaskRestTjeneste;

@ApplicationPath(ApiConfig.API_URI)
public class ApiConfig extends Application {

    private static final Environment ENV = Environment.current();

    public static final String API_URI = "/api";

    public ApiConfig() {

        var oas = new OpenAPI();
        var info = new Info().title("FPLOS").version("1.0").description("REST grensesnitt for fplos.");
        oas.info(info).addServersItem(new Server().url(ENV.getProperty("context.path", "/fplos")));
        var oasConfig = new SwaggerConfiguration().openAPI(oas)
            .prettyPrint(true)
            .resourceClasses(ApiConfig.getAllClasses().stream().map(Class::getName).collect(Collectors.toSet()));
        try {
            new GenericOpenApiContextBuilder<>().openApiConfiguration(oasConfig).buildContext(true).read();
        } catch (OpenApiConfigurationException e) {
            throw new TekniskException("OPENAPI", e.getMessage(), e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        // eksponert grensesnitt
        Set<Class<?>> classes = new HashSet<>(getAllClasses());

        // Autentisering
        classes.add(AuthenticationFilter.class);

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
        classes.add(ReservasjonRestTjeneste.class);
        classes.add(NøkkeltallRestTjeneste.class);
        classes.add(AvdelingslederRestTjeneste.class);
        classes.add(AvdelingslederOppgaveRestTjeneste.class);
        classes.add(AdminRestTjeneste.class);
        classes.add(MigreringRestTjeneste.class);
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
