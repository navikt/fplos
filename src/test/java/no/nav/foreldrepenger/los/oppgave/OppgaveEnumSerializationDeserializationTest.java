package no.nav.foreldrepenger.los.oppgave;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class OppgaveEnumSerializationDeserializationTest {
    private static final ObjectMapper mapper = getObjectMapper();

    @Test
    void fagsakStatus() throws Exception {
        testRoundtrip(FagsakStatus.LØPENDE);
    }

    @Test
    void andreKriterierType() throws Exception {
        testRoundtrip(AndreKriterierType.TIL_BESLUTTER);
    }

    @Test
    void behandlingType() throws Exception {
        testRoundtrip(BehandlingType.FØRSTEGANGSSØKNAD);
    }

    private void testRoundtrip(Object initiell) throws JsonProcessingException {
        var json = toJson(initiell);
        var roundtripped = fromJson(json, initiell.getClass());
        assertThat(initiell).isEqualTo(roundtripped);
    }

    static String toJson(Object dto) throws JsonProcessingException {
        return mapper.writeValueAsString(dto);
    }

    static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    static ObjectMapper getObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
