package no.nav.foreldrepenger.los.web.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.Map;
import java.util.Set;


@ApplicationPath("/fplos")
public class ForwarderApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(FpsakForwarder.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }
}
