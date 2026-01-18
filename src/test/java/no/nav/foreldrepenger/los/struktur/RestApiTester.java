package no.nav.foreldrepenger.los.struktur;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.los.konfig.ApiConfig;
import no.nav.foreldrepenger.los.konfig.ForvaltningApiConfig;

public class RestApiTester {

    static Collection<Method> finnAlleRestMetoder() {
        return finnAktuelleRestTjenester()
            .map(Class::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .toList();
    }

    static Stream<Class<?>> finnAktuelleRestTjenester() {
        var resultat = new LinkedHashSet<>(finnAktuelleRestTjenester(new ApiConfig()));
        resultat.addAll(finnAktuelleRestTjenester(new ForvaltningApiConfig()));
        return resultat.stream();
    }

    private static Collection<Class<?>> finnAktuelleRestTjenester(Application config) {
        return config.getClasses().stream()
            .filter(c -> c.getAnnotation(Path.class) != null)
            .filter(c  -> !c.equals(OpenApiResource.class))
            .toList();
    }
}
