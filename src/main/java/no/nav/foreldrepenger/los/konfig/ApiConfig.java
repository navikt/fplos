package no.nav.foreldrepenger.los.konfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import no.nav.foreldrepenger.konfig.Environment;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ApplicationPath;
import no.nav.foreldrepenger.los.server.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.los.server.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.los.server.exceptions.JsonParseExceptionMapper;
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

@ApplicationPath(ApiConfig.API_URI)
public class ApiConfig extends ResourceConfig {

    public static final String API_URI = "/api";

    public ApiConfig() {
        registerClasses(getFellesConfigClasses());
        registerClasses(getApplicationClasses());
        setProperties(getApplicationProperties());
    }

    private static Set<Class<?>> getApplicationClasses() {
        return Set.of(KodeverkRestTjeneste.class, DriftsmeldingerRestTjeneste.class, SaksbehandlerSakslisteRestTjeneste.class,
            OppgaveRestTjeneste.class, AvdelingslederSakslisteRestTjeneste.class, AvdelingslederSaksbehandlerRestTjeneste.class,
            AvdelingReservasjonerRestTjeneste.class, ReservasjonRestTjeneste.class, NøkkeltallRestTjeneste.class, AvdelingslederRestTjeneste.class,
            AvdelingslederOppgaveRestTjeneste.class, MigreringRestTjeneste.class);
    }

    static Set<Class<?>> getFellesConfigClasses() {
        return Set.of(AuthenticationFilter.class, // Autentisering
            GeneralRestExceptionMapper.class, // Exception handling
            ConstraintViolationMapper.class, // Exception handling
            JacksonJsonConfig.class // Json
        );
    }


    private static Map<String, Object> getApplicationProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Ref Jersey doc
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        return properties;
    }

}
