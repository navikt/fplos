package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateDeserializerTest {
    private final LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer();
    private final JsonParser jsonParser = mock(JsonParser.class);
    private final DeserializationContext deserializationContext = mock(DeserializationContext.class);

    @Test
    public void testDeserialzieIsOk() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("2020-01-02");
        LocalDate result = localDateDeserializer.deserialize(jsonParser, deserializationContext);
        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(2020);
        assertThat(result.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(result.getDayOfMonth()).isEqualTo(2);
    }

    @Test
    public void testDeserializeIsNotOk() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("01.01.2020");
        assertThrows(DateTimeParseException.class, () -> localDateDeserializer.deserialize(jsonParser, deserializationContext));
    }
}
