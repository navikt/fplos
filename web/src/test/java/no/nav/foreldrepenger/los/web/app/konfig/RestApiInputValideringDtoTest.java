package no.nav.foreldrepenger.los.web.app.konfig;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Fail.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;
import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeverk;
import no.nav.foreldrepenger.los.web.app.IndexClasses;

class RestApiInputValideringDtoTest extends RestApiTester {

    private final Set<Class<?>> validerteKlasser = new HashSet<>(); // trengs for å unngå løkker og unngå å validere samme klasse flere ganger dobbelt

    /**
     * IKKE ignorer eller fjern denne testen, den sørger for at inputvalidering er i orden for REST_MED_INNTEKTSMELDING-grensesnittene
     * <p>
     * Kontakt Team Humle hvis du trenger hjelp til å endre koden din slik at den går igjennom her
     */
    @ParameterizedTest
    @MethodSource("finnAlleDtoTyper")
    void alle_felter_i_objekter_som_brukes_som_inputDTO_skal_enten_ha_valideringsannotering_eller_være_av_godkjent_type(Class<?> dto) throws Exception {
        validerRekursivt(validerteKlasser, dto, null);
    }

    private static final List<Class<? extends Object>> ALLOWED_ENUM_ANNOTATIONS = Arrays.asList(JsonProperty.class, JsonValue.class, JsonIgnore.class,
        Valid.class, Null.class, NotNull.class, ValidKodeverk.class);

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List<List<Class<? extends Annotation>>>> UNNTATT_FRA_VALIDERING = new HashMap<>() {
        {

            put(boolean.class, singletonList(emptyList()));
            put(Boolean.class, singletonList(emptyList()));

            // LocalDate og LocalDateTime har egne deserializers
            put(LocalDate.class, singletonList(emptyList()));
            put(LocalDateTime.class, singletonList(emptyList()));

            // Enforces av UUID selv
            put(UUID.class, singletonList(emptyList()));
        }
    };

    @SuppressWarnings("rawtypes")
    private static final Map<Class, List<List<Class<? extends Annotation>>>> VALIDERINGSALTERNATIVER = new HashMap<>() {
        {
            put(String.class, asList(asList(Pattern.class, Size.class), singletonList(Pattern.class), singletonList(Digits.class)));
            put(Long.class, asList(asList(Min.class, Max.class), asList(Digits.class)));
            put(long.class, asList(asList(Min.class, Max.class), asList(Digits.class)));
            put(Integer.class, singletonList(asList(Min.class, Max.class)));
            put(int.class, singletonList(asList(Min.class, Max.class)));
            put(BigDecimal.class, asList(asList(Min.class, Max.class, Digits.class), asList(DecimalMin.class, DecimalMax.class, Digits.class)));

            putAll(UNNTATT_FRA_VALIDERING);
        }
    };

    private static List<List<Class<? extends Annotation>>> getVurderingsalternativer(Field field) {
        var type = field.getType();
        if (field.getType().isEnum()) {
            return Collections.singletonList(Collections.singletonList(Valid.class));
        }
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            if (brukerGenerics(field)) {
                var args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (Arrays.stream(args).allMatch(UNNTATT_FRA_VALIDERING::containsKey)) {
                    return Collections.singletonList(Arrays.asList(Size.class));
                }
            }
            return singletonList(Arrays.asList(Valid.class, Size.class));
        }
        return VALIDERINGSALTERNATIVER.get(type);
    }

    private static Set<Class<?>> finnAlleDtoTyper() {
        Set<Class<?>> parametre = new TreeSet<>(Comparator.comparing(Class::getName));
        for (var method : finnAlleRestMetoder()) {
            parametre.addAll(Arrays.asList(method.getParameterTypes()));
            for (var type : method.getGenericParameterTypes()) {
                if (type instanceof ParameterizedType genericTypes) {
                    for (var gen : genericTypes.getActualTypeArguments()) {
                        parametre.add((Class<?>) gen);
                    }
                }
            }
        }
        Set<Class<?>> filtreteParametre = new TreeSet<>(Comparator.comparing(Class::getName));
        for (var klasse : parametre) {
            if (klasse.getName().startsWith("java") || klasse.isInterface()) {
                // ikke sjekk nedover i innebygde klasser, det skal brukes annoteringer på tidligere tidspunkt
                continue;
            }
            filtreteParametre.add(klasse);
        }
        return filtreteParametre;
    }

    private static void validerRekursivt(Set<Class<?>> besøkteKlasser, Class<?> klasse, Class<?> forrigeKlasse) throws URISyntaxException {
        if (erKodeverk(klasse)) {
            return;
        }

        if (besøkteKlasser.contains(klasse)) {
            return;
        }

        var protectionDomain = klasse.getProtectionDomain();
        var codeSource = protectionDomain.getCodeSource();
        if (codeSource == null) {
            // system klasse
            return;
        }

        besøkteKlasser.add(klasse);
        if (klasse.getAnnotation(Entity.class) != null || klasse.getAnnotation(MappedSuperclass.class) != null) {
            fail("Klassen " + klasse + " er en entitet, kan ikke brukes som DTO. Brukes i " + forrigeKlasse);
        }

        var klasseLocation = codeSource.getLocation();
        for (var subklasse : IndexClasses.getIndexFor(klasseLocation.toURI()).getSubClassesWithAnnotation(klasse, JsonTypeName.class)) {
            validerRekursivt(besøkteKlasser, subklasse, forrigeKlasse);
        }
        for (var field : getRelevantFields(klasse)) {
            if (field.getAnnotation(JsonIgnore.class) != null) {
                continue; // feltet blir hverken serialisert elle deserialisert, unntas fra sjekk
            }
            if (field.getType().isEnum()) {
                validerEnum(field);
                continue; // enum er OK
            }
            if (erKodeverk(field.getType())) {
                validerHarValidkodelisteAnnotering(field);
            } else if (getVurderingsalternativer(field) != null) {
                validerRiktigAnnotert(field); // har konfigurert opp spesifikk validering
            } else if (field.getType().getName().startsWith("java")) {
                fail("Feltet " + field + " har ikke påkrevde annoteringer. Trenger evt. utvidelse av denne testen for å akseptere denne typen.");
            } else {
                validerHarValidAnnotering(field);
                validerRekursivt(besøkteKlasser, field.getType(), forrigeKlasse);
            }
            if (brukerGenerics(field)) {
                validerRekursivt(besøkteKlasser, field.getType(), forrigeKlasse);
                for (var klazz : genericTypes(field)) {
                    validerRekursivt(besøkteKlasser, klazz, forrigeKlasse);
                }
            }
        }
    }

    private static void validerHarValidkodelisteAnnotering(Field field) {
        if (field.getAnnotation(ValidKodeverk.class) == null) {
            fail("Feltet " + field + " er et kodeverk, og må ha @ValidKodeverk-annotering");
        }
    }

    private static void validerEnum(Field field) {
        if (!erKodeverk(field.getType())) {
            validerRiktigAnnotert(field);
        }
        var illegal = Arrays.stream(field.getAnnotations()).filter(a -> !ALLOWED_ENUM_ANNOTATIONS.contains(a.annotationType())).toList();
        if (!illegal.isEmpty()) {
            fail("Ugyldige annotasjoner funnet på [" + field + "]: " + illegal);
        }

    }

    private static void validerHarValidAnnotering(Field field) {
        if (field.getAnnotation(Valid.class) == null) {
            fail("Feltet " + field + " må ha @Valid-annotering.");
        }
    }

    private static Set<Class<?>> genericTypes(Field field) {
        Set<Class<?>> klasser = new HashSet<>();
        var type = (ParameterizedType) field.getGenericType();
        for (var t : type.getActualTypeArguments()) {
            klasser.add((Class<?>) t);
        }
        return klasser;
    }

    private static boolean brukerGenerics(Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    private static Set<Field> getRelevantFields(Class<?> klasse) {
        Set<Field> fields = new LinkedHashSet<>();
        while (!klasse.isPrimitive() && !klasse.getName().startsWith("java")) {
            fields.addAll(fjernStaticFields(Arrays.asList(klasse.getDeclaredFields())));
            klasse = klasse.getSuperclass();
        }
        return fields;
    }

    private static Collection<Field> fjernStaticFields(List<Field> fields) {
        return fields.stream().filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
    }

    private static void validerRiktigAnnotert(Field field) {
        var alternativer = getVurderingsalternativer(field);
        for (var alternativ : alternativer) {
            var harAlleAnnoteringerForAlternativet = true;
            for (var annotering : alternativ) {
                if (field.getAnnotation(annotering) == null) {
                    harAlleAnnoteringerForAlternativet = false;
                }
            }
            if (harAlleAnnoteringerForAlternativet) {
                validerRiktigAnnotertForCollectionsAndMaps(field);
                return;
            }
        }
        throw new IllegalArgumentException("Feltet " + field + " har ikke påkrevde annoteringer: " + alternativer);
    }

    private static void validerRiktigAnnotertForCollectionsAndMaps(Field field) {
        if (!Properties.class.isAssignableFrom(field.getType()) && (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(
            field.getType()))) {
            var annType = (AnnotatedParameterizedType) field.getAnnotatedType();
            var annotatedTypes = annType.getAnnotatedActualTypeArguments();
            for (var at : List.of(annotatedTypes)) {
                if (erKodeverk(at.getType())) {
                    if (!at.isAnnotationPresent(ValidKodeverk.class)) {
                        throw new IllegalArgumentException(
                            "Feltet " + field + " har ikke påkrevd annotering for kodeverk: @" + ValidKodeverk.class.getSimpleName());
                    }
                }
            }
        }
    }

    private static boolean erKodeverk(Type... args) {
        return Kodeverdi.class.isAssignableFrom((Class<?>) args[0]);
    }
}
