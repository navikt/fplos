package no.nav.foreldrepenger.los.struktur;


import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.konfig.IndexClasses;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

class SjekkDtoStrukturTest {
    private static final List<String> SKIPPED = Arrays.asList("class", "kode");

    @ParameterizedTest
    @MethodSource("parameters")
    void skal_ha_riktig_navn_på_properties_i_dto_eller_konfiguret_med_annotations(Class<?> cls) throws Exception {
        sjekkJsonProperties(cls);
    }

    private static Collection<Class<?>> parameters() throws URISyntaxException {
        var indexClasses = IndexClasses.getIndexFor(IndexClasses.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return indexClasses.getClasses(ci -> ci.name().toString().endsWith("Dto"), c -> !c.isInterface());
    }

    private void sjekkJsonProperties(Class<?> c) throws IntrospectionException {
        var fields = Arrays.asList(c.getDeclaredFields());
        var fieldNames = fields.stream()
            .filter(f -> !f.isSynthetic() && !Modifier.isStatic(f.getModifiers()))
            .filter(f -> f.getAnnotation(JsonProperty.class) == null)
            .filter(f -> f.getAnnotation(JsonValue.class) == null)
            .filter(f -> f.getAnnotation(JsonIgnore.class) == null)
            .map(f -> f.getName())
            .collect(Collectors.toSet());

        if (!fieldNames.isEmpty()) {
            for (var prop : Introspector.getBeanInfo(c, c.getSuperclass()).getPropertyDescriptors()) {
                if (prop.getReadMethod() != null) {
                    var readName = prop.getReadMethod();
                    var propName = prop.getName();
                    if (!SKIPPED.contains(propName)) {
                        if (readName.getAnnotation(JsonIgnore.class) == null && readName.getAnnotation(JsonProperty.class) == null) {
                            assertThat(propName).as(
                                "Gettere er ikke samstemt med felt i klasse, sørg for matchende bean navn og return type eller bruk @JsonProperty/@JsonIgnore/@JsonValue til å sette navn for json struktur: "
                                    + c.getName()).isIn(fieldNames);
                        }
                    }
                }

                if (prop.getWriteMethod() != null) {
                    var readName = prop.getWriteMethod();
                    var propName = prop.getName();
                    if (!SKIPPED.contains(propName)) {
                        if (readName.getAnnotation(JsonIgnore.class) == null && readName.getAnnotation(JsonProperty.class) == null) {
                            assertThat(propName).as(
                                "Settere er ikke samstemt med felt i klasse, sørg for matchende bean navn og return type eller bruk @JsonProperty/@JsonIgnore/@JsonValue til å sette navn for json struktur: "
                                    + c.getName()).isIn(fieldNames);
                        }
                    }
                }
            }
        }
    }

}
