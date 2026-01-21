package no.nav.foreldrepenger.los.struktur;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import no.nav.foreldrepenger.los.konfig.ApiConfig;
import no.nav.foreldrepenger.los.konfig.ForvaltningApiConfig;

public class RestApiTester {

    private static final Set<Class<? extends Annotation>> REST_METHOD_ANNOTATIONS = Set.of(GET.class, POST.class, DELETE.class, PATCH.class, PUT.class);

    static Collection<Method> finnAlleRestMetoder() {
        return finnAktuelleRestTjenester()
            .map(Class::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .filter(RestApiTester::erMetodenEtRestEndepunkt)
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

    private static boolean erMetodenEtRestEndepunkt(Method method) {
        return REST_METHOD_ANNOTATIONS.stream().anyMatch(method::isAnnotationPresent);
    }
}
