package no.nav.foreldrepenger.loslager.testutilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.fplos.kodeverk.KodeverkTabell;


public class KodeverkFraJson {

    public static final String FILE_NAME_PREFIX = "kodeverk_";
    public static final String FILE_NAME_SUFFIX = ".json";

    private static final ObjectMapper mapper;

    static {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new Jdk8Module());
        om.registerModule(new JavaTimeModule());
        om.registerModule(new MyModule());
        // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);
        mapper = om;
    }

    private static final Map<Class<?>, List<?>> CACHE = new HashMap<>();

    public KodeverkFraJson() {

    }

    @SuppressWarnings("unchecked")
    public synchronized <V> List<V> lesKodeverkFraFil(Class<?> cls) {
        if (CACHE.containsKey(cls)) {
            return (List<V>) CACHE.get(cls);
        }
        String name;
        if (cls.isAnnotationPresent(DiscriminatorValue.class)) {
            name = cls.getAnnotation(DiscriminatorValue.class).value();
        } else if (cls.isAnnotationPresent(Entity.class)) {
            name = cls.getAnnotation(Entity.class).name();
        } else {
            throw new IllegalArgumentException("Mangler @Entity eller @DiscriminatorValue på " + cls);
        }

        String fullName = FILE_NAME_PREFIX + name + FILE_NAME_SUFFIX;
        try (InputStream is = getClass().getResourceAsStream("/" + fullName);) {
            try (MappingIterator<Object> readValues = mapper.readerFor(cls).readValues(is);) {

                List<V> read = new ArrayList<>();

                while (readValues.hasNext()) {
                    read.add((V) readValues.next());
                }
                CACHE.put(cls, read);
                return read;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Kunne ikke lese " + fullName + " for class:" + cls, e);
        }

    }

    public static class MyModule extends SimpleModule {
        @SuppressWarnings("deprecation")
        public MyModule() {
            super("ModuleName", new Version(0, 0, 1, null));
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(KodeverkTabell.class, KodeverkFraJson.PropertyFilterKodeverkTabellMixIn.class);
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "kode")
    public static class PropertyFilterKodeverkTabellMixIn {
    }

}
