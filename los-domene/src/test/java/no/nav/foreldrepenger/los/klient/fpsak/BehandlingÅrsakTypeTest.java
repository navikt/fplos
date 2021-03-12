package no.nav.foreldrepenger.los.klient.fpsak;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingÅrsakType;


public class BehandlingÅrsakTypeTest {
    private static final ObjectMapper mapper = getObjectMapper();


    @Test
    public void behandlingÅrsakType() throws Exception {
        var json = "{\"kode\": \"RE-END-FRA-BRUKER\"}";
        var objektet = fromJson(json, BehandlingÅrsakType.class);
        assertThat(objektet).isEqualTo(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    @Test
    public void ukjentVerdiDeserialiseresTilNull() throws Exception {
        var ukjentKode = "{\"kode\": \"DUMMY-UKJENT-VERDI\"}";
        assertThat(fromJson(ukjentKode, BehandlingÅrsakType.class)).isNull();
    }

    static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    static ObjectMapper getObjectMapper() { // hentet fra felles-integrasjon-rest-klient
        var mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

}
