package no.nav.foreldrepenger.los.web.app.konfig;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.los.konfig.ApiConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RestApiTester {

    static Collection<Method> finnAlleRestMetoder() {
        return finnAktuelleRestTjenester(new ApiConfig()).stream()
                                                         .map(Class::getDeclaredMethods)
                                                         .flatMap(Arrays::stream)
                                                         .filter(m -> Modifier.isPublic(m.getModifiers()))
                                                         .collect(Collectors.toList());
    }

    private static Collection<Class<?>> finnAktuelleRestTjenester(Application config) {
        final Predicate<Class<?>> ikkeEnUnntattTjeneste = c -> !c.equals(OpenApiResource.class);
        return config.getClasses()
            .stream()
            .filter(c -> c.getAnnotation(Path.class) != null)
            .filter(ikkeEnUnntattTjeneste)
            .collect(Collectors.toList());
    }
}
