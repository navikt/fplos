package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class BehandlingÅrsakTypeTest {
    private static final ObjectMapper mapper = getObjectMapper();


    @Test
    public void behandlingÅrsakType() throws Exception {
        String json = "{\"kode\": \"RE-END-FRA-BRUKER\"}";
        var objektet = fromJson(json, BehandlingÅrsakType.class);
        assertEquals(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER, objektet);
    }

    @Test
    public void ukjentVerdiDeserialiseresTilNull() throws Exception {
        String ukjentKode = "{\"kode\": \"DUMMY-UKJENT-VERDI\"}";
        assertEquals(null, fromJson(ukjentKode, BehandlingÅrsakType.class));
    }

    static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    static ObjectMapper getObjectMapper() { // hentet fra felles-integrasjon-rest-klient
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

}
