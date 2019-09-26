package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.OppgaveAktør;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt.BooleanAttributt;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt.HeltallAttributt;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.OppgaveEvent;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fplos.kafkatjenester.Deserialiser.deserialiser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OppgaveEventTest {

    @Test
    public void testDeserialingOppgaveEvent() throws IOException, URISyntaxException {
        String json = readFile("oppgaveevent.json");
        OppgaveEvent event = deserialiser(json, OppgaveEvent.class);
        assertNotNull(event);
    }

    @Test
    public void testOppgaveEventRoundtrip() throws Exception {
        OppgaveEvent event = OppgaveEvent.Builder.newBuilder()
                .withUuid("34934-349890243-239489")
                .withAktoerId(List.of(new OppgaveAktør("12345", OppgaveAktør.AktørRolle.SØKER)))
                .withBehandlendeEnhet("9834")
                .withBehandlingType("TILBAKEKREVING")
                .withFagsystem("FAGSYSTEM")
                .withFagsystemSaksnummer("SAKSNUMMER")
                .withOppgaveAktiveres(true)
                .withAttributter(List.of(new HeltallAttributt("Utestående beløp", 8437), new BooleanAttributt("Til beslutter", true)))
                .withUrl("https://app.adeo.no/fplos/4932934")
                .withYtelsestype("FP")
                .withHendelseTid(LocalDateTime.now())
                .build();
        String json = serialiserToJson(event);
        System.out.println(json);
        OppgaveEvent roundtrippedEvent = deserialiser(json, OppgaveEvent.class);
        assertEquals(event, roundtrippedEvent);
    }

    public String readFile(String filename) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
        return Files.readString(path);
    }

    private String serialiserToJson(Object objekt) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(objekt);
    }
}
