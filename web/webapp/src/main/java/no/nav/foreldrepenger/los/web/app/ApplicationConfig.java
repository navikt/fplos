package no.nav.foreldrepenger.los.web.app;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
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
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave.AvdelingslederOppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.reservasjoner.AvdelingReservasjonerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fpsak.FpsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.KodeverkRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.konfig.KonfigRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.NavAnsattRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall.SaksbehandlerNøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste.SaksbehandlerSakslisteRestTjeneste;
import no.nav.vedtak.konfig.PropertyUtil;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    public ApplicationConfig() {

        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title("Foreldrepenger risikoklassifisering")
                .version("1.0")
                .description("REST grensesnitt for fplos.");

        oas.info(info)
                .addServersItem(new Server()
                        .url("/fplos"));
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
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



    private boolean disableSsl() {
        if(PropertyUtil.getProperty("disable.ssl") == null){
            return false;
        }
        return PropertyUtil.getProperty("disable.ssl").contentEquals("true");
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(FagsakRestTjeneste.class);
        classes.add(NavAnsattRestTjeneste.class);
        classes.add(KonfigRestTjeneste.class);
        classes.add(KodeverkRestTjeneste.class);
        classes.add(SaksbehandlerSakslisteRestTjeneste.class);
        classes.add(OppgaveRestTjeneste.class);
        classes.add(AvdelingslederSakslisteRestTjeneste.class);
        classes.add(AvdelingslederSaksbehandlerRestTjeneste.class);
        classes.add(AvdelingReservasjonerRestTjeneste.class);
        classes.add(NøkkeltallRestTjeneste.class);
        classes.add(AvdelingslederRestTjeneste.class);
        classes.add(AvdelingslederOppgaveRestTjeneste.class);
        classes.add(AdminRestTjeneste.class);
        classes.add(SaksbehandlerNøkkeltallRestTjeneste.class);
        classes.add(FpsakRestTjeneste.class);

        classes.add(ConstraintViolationMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(JsonParseExceptionMapper.class);
        classes.add(GeneralRestExceptionMapper.class);
        classes.add(JacksonJsonConfig.class);

        classes.add(SwaggerSerializers.class);
        classes.add(OpenApiResource.class);

        return Collections.unmodifiableSet(classes);
    }
}
