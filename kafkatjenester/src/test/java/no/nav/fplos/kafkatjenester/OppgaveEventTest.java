package no.nav.fplos.kafkatjenester;

import no.nav.fplos.kafkatjenester.genereltgrensesnitt.Attributt;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.HeltallAttributt;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.OppgaveEvent;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static no.nav.fplos.kafkatjenester.Deserialiser.deserialiser;

public class OppgaveEventTest {

    @Test
    public void testDeserialingOppgaveEvent() throws IOException, URISyntaxException {
        String json = readFile("oppgaveevent.json");
        OppgaveEvent event = deserialiser(json, OppgaveEvent.class);
        //assertThat(event.getAttributter()).isEqualTo(Map.of("FEILUTBETALT_BELØP", 1500));
        for (Attributt attributt : event.getAttributter()) {
            if (attributt instanceof HeltallAttributt) {
                var heltall = HeltallAttributt.class.cast(attributt);
                System.out.println(heltall);
            }
        }
        System.out.println(event.toString());
    }

    public String readFile(String filename) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
        return Files.readString(path);
    }
}
