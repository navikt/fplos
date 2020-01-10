package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KafkaReaderTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private FpsakEventHandler fpsakEventHandler = new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient);
    private TilbakekrevingEventHandler tilbakekrevingEventHandler = new TilbakekrevingEventHandler(oppgaveRepository);
    private JsonOppgaveHandler jsonOppgaveHandler = new JsonOppgaveHandler();
    private KafkaReader kafkaReader = new KafkaReader(null, fpsakEventHandler, tilbakekrevingEventHandler, oppgaveRepository, jsonOppgaveHandler);
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

    private String getJson(BehandlingProsessEventDto produksjonstyringEventDto) throws IOException {
        Writer jsonWriter = new StringWriter();
        objectMapper.writeValue(jsonWriter, produksjonstyringEventDto);
        jsonWriter.flush();
        return jsonWriter.toString();
    }

    private BehandlingFpsak lagBehandlingDto() {
        return BehandlingFpsak.builder()
                .medUuid(UUID.nameUUIDFromBytes("TEST".getBytes()))
                .medBehandlendeEnhetNavn("NAV")
                .medHarRefusjonskravFraArbeidsgiver(false)
                .medAksjonspunkter(Collections.singletonList(new Aksjonspunkt.Builder()
                        .medDefinisjon("5401")
                        .medStatus("UTRED")
                        .medFristTid(aksjonspunktFrist)
                        .build()))
                .build();
    }
}
