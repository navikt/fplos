package no.nav.foreldrepenger.loslager.oppgave;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;


import static org.junit.Assert.*;

public class OppgaveEnumSerializationDeserializationTest {
    private static final ObjectMapper mapper = getObjectMapper();

    @Test
    public void fagsakStatus_roundtrip() throws Exception {
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

    @Test
    public void eventMottak() throws Exception {
        testRoundtrip(EventmottakStatus.FEILET);
    }

    @Test
    public void fagsystem() throws Exception {
        testRoundtrip(Fagsystem.INFOTRYGD);
    }

    @Test
    public void køSortering() throws Exception {
        testRoundtrip(KøSortering.FORSTE_STONADSDAG);
    }

    private void testRoundtrip(Object initiell) throws JsonProcessingException {
        String json = toJson(initiell);
        assertEquals(initiell, fromJson(json, initiell.getClass()));
    }

    static String toJson(Object dto) throws JsonProcessingException {
        return mapper.writeValueAsString(dto);
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
