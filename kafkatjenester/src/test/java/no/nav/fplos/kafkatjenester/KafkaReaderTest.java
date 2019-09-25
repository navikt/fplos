package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProviderImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.OppgaveEvent;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static no.nav.fplos.kafkatjenester.Deserialiser.deserialiser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KafkaReaderTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepositoryProvider oppgaveRepositoryProvider = new OppgaveRepositoryProviderImpl(entityManager );
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private FpsakEventHandler fpsakEventHandler = new FpsakEventHandler(oppgaveRepositoryProvider, foreldrepengerBehandlingRestKlient);
    private JsonOppgaveHandler jsonOppgaveHandler = new JsonOppgaveHandler(oppgaveRepositoryProvider, foreldrepengerBehandlingRestKlient);

    private KafkaReader kafkaReader = new KafkaReader(null, jsonOppgaveHandler, fpsakEventHandler, oppgaveRepositoryProvider);
    private FpsakEventHandlerTest fpsakEventHandlerTest = new FpsakEventHandlerTest();
    private ObjectMapper objectMapper = new ObjectMapper();

    private LocalDateTime aksjonspunktFrist = null;

    @Test
    public void testOk() throws IOException {
        BehandlingProsessEventDto behandlingProsessEventDto = fpsakEventHandlerTest.eventDrammenFra(fpsakEventHandlerTest.aksjonspunktKoderSkalHaOppgave);
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());
        kafkaReader.prosesser(getJson(behandlingProsessEventDto));
        assertThat(repoRule.getRepository().hentAlle(EventmottakFeillogg.class)).hasSize(0);
    }

    @Test
    public void testFeilet() {
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());
        kafkaReader.prosesser("EN HELTFEIL MELDINGGGGG");
        assertThat(repoRule.getRepository().hentAlle(EventmottakFeillogg.class)).hasSize(1);
    }

    @Test
    public void testDeserialingOppgaveEvent() throws IOException, URISyntaxException {
        String json = readFile("oppgaveevent.json");
        OppgaveEvent event = deserialiser(json, OppgaveEvent.class);
        //assertThat(event.getAttributter()).isEqualTo(Map.of("FEILUTBETALT_BELØP", 1500));
        System.out.println(event.toString());

    }

    public String readFile(String filename) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
        return Files.readString(path);
    }

    private String getJson(BehandlingProsessEventDto produksjonstyringEventDto) throws IOException {
        Writer jsonWriter = new StringWriter();
        objectMapper.writeValue(jsonWriter, produksjonstyringEventDto);
        jsonWriter.flush();
        return jsonWriter.toString();
    }

    private BehandlingFpsak lagBehandlingDto() {
        return BehandlingFpsak.builder()
                .medBehandlendeEnhetNavn("NAV")
                .medHarRefusjonskrav(false)
                .medAksjonspunkter(Collections.singletonList(new AksjonspunktDto.Builder()
                        .medDefinisjon("5401")
                        .medStatus("UTRED")
                        .medFristTid(aksjonspunktFrist)
                        .build()))
                .build();
    }



}
