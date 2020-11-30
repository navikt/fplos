package no.nav.foreldrepenger.loslager.oppgave;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class OppgaveEnumSerializationDeserializationTest {
    private static final ObjectMapper mapper = getObjectMapper();

    @Test
    public void fagsakStatus() throws Exception {
        testRoundtrip(FagsakStatus.LØPENDE);
    }

    @Test
    public void andreKriterierType() throws Exception {
        testRoundtrip(AndreKriterierType.TIL_BESLUTTER);
    }

    @Test
    public void behandlingStatus() throws Exception {
        testRoundtrip(BehandlingStatus.AVSLUTTET);
    }

    @Test
    public void behandlingType() throws Exception {
        testRoundtrip(BehandlingType.FØRSTEGANGSSØKNAD);
    }

    private void testRoundtrip(Object initiell) throws JsonProcessingException {
        String json = toJson(initiell);
        Object roundtripped = fromJson(json, initiell.getClass());
        assertThat(initiell).isEqualTo(roundtripped);
    }

    static String toJson(Object dto) throws JsonProcessingException {
        return mapper.writeValueAsString(dto);
    }

    static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
