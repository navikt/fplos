package no.nav.foreldrepenger.los.web.app;

import io.swagger.jaxrs.config.BeanConfig;
import no.nav.foreldrepenger.los.web.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.los.web.app.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.los.web.app.tjenester.admin.AdminRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.kodeverk.KodeverkRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.NavAnsattRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.nøkkeltall.SaksbehandlerNøkkeltallRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste.SaksbehandlerSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave.AvdelingslederOppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.FagsakRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.konfig.KonfigRestTjeneste;
import no.nav.vedtak.konfig.PropertyUtil;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    public static final String API_URI = "/api";

    public ApplicationConfig() {

        BeanConfig swaggerConfig = new BeanConfig();
        swaggerConfig.setVersion("1.0");
        if (disableSsl()) {
            swaggerConfig.setSchemes(new String[]{"http"});
        } else {
            swaggerConfig.setSchemes(new String[]{"http","https"});
        }
        swaggerConfig.setBasePath("/fplos/api");
        swaggerConfig.setResourcePackage("no.nav");
        swaggerConfig.setTitle("FPLOS");
        swaggerConfig.setDescription("REST grensesnitt for FPLOS.");
        swaggerConfig.setScan(true);
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
        classes.add(NøkkeltallRestTjeneste.class);
        classes.add(AvdelingslederRestTjeneste.class);
        classes.add(AvdelingslederOppgaveRestTjeneste.class);
        classes.add(AdminRestTjeneste.class);
        classes.add(SaksbehandlerNøkkeltallRestTjeneste.class);

        classes.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        classes.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        classes.add(ConstraintViolationMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(JsonParseExceptionMapper.class);
        classes.add(GeneralRestExceptionMapper.class);
        classes.add(JacksonJsonConfig.class);

        return Collections.unmodifiableSet(classes);
    }
}
