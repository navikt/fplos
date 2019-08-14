package no.nav.foreldrepenger.los.web.app.konfig;

import no.nav.foreldrepenger.los.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.los.web.app.jackson.JacksonJsonConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FellesKlasserForRest {

    private static final Set<Class<?>> CLASSES;

    static {
        Set<Class<?>> klasser = new HashSet<>();
        klasser.add(JacksonJsonConfig.class);
        klasser.add(GeneralRestExceptionMapper.class);
        CLASSES = Collections.unmodifiableSet(klasser);
    }

    private FellesKlasserForRest() {

    }

    public static Collection<Class<?>> getClasses() {
        return CLASSES;
    }
}
