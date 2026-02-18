package no.nav.foreldrepenger.los.konfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.glassfish.jersey.server.ServerProperties;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
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
public class ApiConfig extends Application {

    public static final String API_URI = "/api";

    @Override
    public Set<Class<?>> getClasses() {
        // eksponert grensesnitt
        Set<Class<?>> classes = new HashSet<>(getAllClasses());

        // Standard Jakarta RS oppsett for filtre og plugins
        classes.addAll(FellesConfigClasses.getFellesContainerFilterClasses());
        classes.addAll(FellesConfigClasses.getFellesRsExtConfigClasses());

        return Collections.unmodifiableSet(classes);
    }

    private static Collection<Class<?>> getAllClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(KodeverkRestTjeneste.class);
        classes.add(SaksbehandlerSakslisteRestTjeneste.class);
        classes.add(OppgaveRestTjeneste.class);
        classes.add(AvdelingslederSakslisteRestTjeneste.class);
        classes.add(AvdelingslederSaksbehandlerRestTjeneste.class);
        classes.add(AvdelingReservasjonerRestTjeneste.class);
        classes.add(ReservasjonRestTjeneste.class);
        classes.add(NøkkeltallRestTjeneste.class);
        classes.add(AvdelingslederRestTjeneste.class);
        classes.add(AvdelingslederOppgaveRestTjeneste.class);
        classes.add(MigreringRestTjeneste.class);
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
