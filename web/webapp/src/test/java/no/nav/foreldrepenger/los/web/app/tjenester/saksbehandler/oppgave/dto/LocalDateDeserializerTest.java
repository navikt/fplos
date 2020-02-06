package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalDateDeserializerTest {
    private static LocalDateDeserializer localDateDeserializer;
    private static JsonParser jsonParser;
    private static DeserializationContext deserializationContext;

    @BeforeClass
    public static void setUp(){
        localDateDeserializer = new LocalDateDeserializer();
        jsonParser = mock(JsonParser.class);
        deserializationContext = mock(DeserializationContext.class);
    }

    @Test
    public void testDeserialzieIsOk() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("2020-01-02");
        LocalDate result = localDateDeserializer.deserialize(jsonParser, deserializationContext);
        assertNotNull(result);
        assertEquals(2020, result.getYear());
        assertEquals(Month.JANUARY, result.getMonth());
        assertEquals(2, result.getDayOfMonth());
    }

    @Test(expected = DateTimeParseException.class)
    public void testDeserializeIsNotOk() throws IOException {
        when(jsonParser.getValueAsString()).thenReturn("01.01.2020");
        LocalDate result = localDateDeserializer.deserialize(jsonParser, deserializationContext);
    }
}
