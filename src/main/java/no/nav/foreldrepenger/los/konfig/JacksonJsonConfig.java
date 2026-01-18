package no.nav.foreldrepenger.los.konfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;


@Provider
public class JacksonJsonConfig implements ContextResolver<ObjectMapper> {

    private static ObjectMapper OM;

    private final ObjectMapper objectMapper;

    public JacksonJsonConfig() {
        objectMapper = createbjectMapper();
    }

    private static synchronized ObjectMapper createbjectMapper() {
        var mapper = OM;
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OM = mapper;
        }
        return mapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }


}
