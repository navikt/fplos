package no.nav.foreldrepenger.los.web.app.konfig;

import no.nav.foreldrepenger.los.web.app.tjenester.NaisRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.SelftestRestTjeneste;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    public static final String API_URL = "/internal";

    public InternalApplication() {
        // CDI
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(NaisRestTjeneste.class);
        classes.add(SelftestRestTjeneste.class);

        return Collections.unmodifiableSet(classes);
    }
}
