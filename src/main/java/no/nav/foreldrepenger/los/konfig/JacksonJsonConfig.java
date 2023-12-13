package no.nav.foreldrepenger.los.konfig;

import java.net.URISyntaxException;
import java.util.List;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.foreldrepenger.los.web.app.IndexClasses;

@Provider
public class JacksonJsonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public JacksonJsonConfig() {
        this(false);
    }

    public JacksonJsonConfig(boolean serialiserKodeverdiSomObjekt) {
        objectMapper = createbjectMapper(createModule(serialiserKodeverdiSomObjekt));
    }

    private ObjectMapper createbjectMapper(SimpleModule simpleModule) {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(simpleModule);

        mapper.registerSubtypes(getJsonTypeNameClasses());
        return mapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

    private static SimpleModule createModule(boolean serialiserKodeverdiSomObjekt) {
        var module = new SimpleModule("VL-REST", new Version(1, 0, 0, null, null, null));

        module.addSerializer(new JacksonKodeverdiSerializer(serialiserKodeverdiSomObjekt));

        return module;
    }


    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Scan subtyper dynamisk fra WAR slik at superklasse slipper å deklarere @JsonSubtypes.
     */
    public static List<Class<?>> getJsonTypeNameClasses() {
        var cls = JacksonJsonConfig.class;
        IndexClasses indexClasses;
        try {
            indexClasses = IndexClasses.getIndexFor(cls.getProtectionDomain().getCodeSource().getLocation().toURI());
            return indexClasses.getClassesWithAnnotation(JsonTypeName.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Kunne ikke konvertere CodeSource location til URI", e);
        }
    }
}
