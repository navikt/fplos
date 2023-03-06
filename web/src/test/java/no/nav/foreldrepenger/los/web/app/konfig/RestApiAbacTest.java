package no.nav.foreldrepenger.los.web.app.konfig;

import static org.assertj.core.api.Fail.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

class RestApiAbacTest {

    /**
     * IKKE ignorer denne testen, sikrer at REST-endepunkter får tilgangskontroll
     */
    @Test
    void alle_restmetoder_er_annotert_med_BeskyttetRessurs() {
        var manglerAnnotering = RestApiTester.finnAlleRestMetoder()
            .stream()
            .filter(m -> m.getAnnotation(BeskyttetRessurs.class) == null)
            .map(m -> m.getDeclaringClass().getName() + "." + m.getName())
            .toList();
        if (!manglerAnnotering.isEmpty()) {
            fail("Mangler @" + BeskyttetRessurs.class.getSimpleName() + "-annotering på " + manglerAnnotering);
        }
    }

    @Test
    void sjekk_at_ingen_metoder_er_annotert_med_dummy_verdier() {
        RestApiTester.finnAlleRestMetoder().forEach(RestApiAbacTest::assertAtIngenBrukerDummyVerdierPåBeskyttetRessurs);
    }

    @Test
    void alle_input_parametre_til_restmetoder_implementer_AbacDto_eller_spesifiserer_AbacDataSupplier() {
        var feilmelding = "Parameter på %s.%s av type %s må implementere " + AbacDto.class.getSimpleName()
            + ", eller være annotatert med @TilpassetAbacAttributt.\n";
        var feilmeldinger = new StringBuilder();

        for (var restMethode : RestApiTester.finnAlleRestMetoder()) {
            for (var parameter : restMethode.getParameters()) {
                var parameterType = parameter.getType();
                var parameterAnnotations = restMethode.getParameterAnnotations();
                if (Collection.class.isAssignableFrom(parameterType)) {
                    var type = (ParameterizedType) parameter.getParameterizedType();
                    @SuppressWarnings("rawtypes") Class<?> aClass = (Class) (type.getActualTypeArguments()[0]);
                    if (!harAbacKonfigurasjon(parameterAnnotations[0], aClass)) {
                        feilmeldinger.append(String.format(feilmelding, restMethode.getDeclaringClass().getSimpleName(), restMethode.getName(),
                            aClass.getSimpleName()));
                    }
                } else {
                    if (!harAbacKonfigurasjon(parameterAnnotations[0], parameterType)) {
                        feilmeldinger.append(String.format(feilmelding, restMethode.getDeclaringClass().getSimpleName(), restMethode.getName(),
                            parameterType.getSimpleName()));
                    }
                }
            }
        }
        if (feilmeldinger.length() > 0) {
            fail("Følgende inputparametre til REST-tjenester mangler AbacDto-impl\n" + feilmeldinger);
        }
    }

    private static boolean harAbacKonfigurasjon(Annotation[] parameterAnnotations, Class<?> parameterType) {
        var ret = AbacDto.class.isAssignableFrom(parameterType) || IgnorerteInputTyper.ignore(parameterType);
        if (!ret) {
            ret = Stream.of(parameterAnnotations).anyMatch(a -> TilpassetAbacAttributt.class.equals(a.annotationType()));
        }
        return ret;
    }

    private static void assertAtIngenBrukerDummyVerdierPåBeskyttetRessurs(Method metode) {
        var klasse = metode.getDeclaringClass();
        var annotation = metode.getAnnotation(BeskyttetRessurs.class);
        if (annotation != null && annotation.actionType() == ActionType.DUMMY) {
            fail(klasse.getSimpleName() + "." + metode.getName() + " Ikke bruk DUMMY-verdi for " + ActionType.class.getSimpleName());
        } else if (annotation != null && annotation.property().isEmpty() && !Set.of(ResourceType.FAGSAK, ResourceType.OPPGAVESTYRING_AVDELINGENHET,
            ResourceType.OPPGAVESTYRING, ResourceType.APPLIKASJON, ResourceType.DRIFT).contains(annotation.resourceType())) {
            fail(klasse.getSimpleName() + "." + metode.getName() + " Resource ligger ikke i AbacAttributter");
        }
    }

    /**
     * Disse typene slipper naturligvis krav om impl av {@link AbacDto}
     */
    enum IgnorerteInputTyper {
        BOOLEAN(Boolean.class.getName()),
        SERVLET(HttpServletRequest.class.getName());

        private String className;

        IgnorerteInputTyper(String className) {
            this.className = className;
        }

        static boolean ignore(Class<?> klasse) {
            return Arrays.stream(IgnorerteInputTyper.values()).anyMatch(e -> e.className.equals(klasse.getName()));
        }
    }

}
