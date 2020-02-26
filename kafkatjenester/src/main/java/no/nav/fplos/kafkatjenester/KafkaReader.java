package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.log.mdc.MDCOperations;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KafkaReader {
    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer;
    private TilbakekrevingEventHåndterer tilbakekrevingEventHandler;
    private AksjonspunktMeldingConsumer meldingConsumer;
    private static final String CALLID_NAME = "Nav-CallId";

    public KafkaReader() {
        //to make proxyable
    }

    @Inject
    public KafkaReader(AksjonspunktMeldingConsumer meldingConsumer,
                       ForeldrepengerEventHåndterer foreldrepengerEventHåndterer,
                       TilbakekrevingEventHåndterer tilbakekrevingEventHandler,
                       OppgaveRepository oppgaveRepository) {
        this.meldingConsumer = meldingConsumer;
        this.foreldrepengerEventHåndterer = foreldrepengerEventHåndterer;
        this.tilbakekrevingEventHandler = tilbakekrevingEventHandler;
        this.oppgaveRepository = oppgaveRepository;
    }

    public void hentOgLagreMeldingene() {
        ConsumerRecords<String, String> records = meldingConsumer.hentConsumerMeldingene();
        for (ConsumerRecord<String, String> record : records) {
            Headers headers = record.headers();
            for (Header header : headers) {
                if (CALLID_NAME.equals(header.key())) {
                    String callId = new String(header.value());
                    MDCOperations.putCallId(callId);
                }
            }
            prosesser(record.value());
        }
        MDCOperations.removeCallId();
        commitMelding();
    }

    private void commitMelding() {
        meldingConsumer.manualCommitSync();
    }

    public void prosesser(String melding) {
        log.info("Mottatt melding med start :" + melding.substring(0, Math.min(melding.length() - 1, 1000)));
        try {
            var event = deserialiser(melding);
            prosesser(event);
        } catch (Exception e) {
            log.warn("Behandling av event feilet. Lagret melding til EventMottakFeillogg for rekjøring.", e);
            lagreFeiletMelding(melding, e.getMessage());
        }
    }

    private void prosesser(BehandlingProsessEventDto event) {
        if (event instanceof TilbakebetalingBehandlingProsessEventDto) {
            var tilbakekrevingEvent = (TilbakebetalingBehandlingProsessEventDto) event;
            tilbakekrevingEventHandler.prosesser(tilbakekrevingEvent);
        } else {
            var fpsakEvent = (FpsakBehandlingProsessEventDto) event;
            foreldrepengerEventHåndterer.prosesser(fpsakEvent);
        }
    }

    private void lagreFeiletMelding(String melding, String feilmelding) {
        oppgaveRepository.lagre(new EventmottakFeillogg(melding, feilmelding));
    }

    private BehandlingProsessEventDto deserialiser(String melding) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.addMixIn(BehandlingProsessEventDto.class, BehandlingProsessEventDtoMixin.class);
        return mapper.readValue(melding, BehandlingProsessEventDto.class);
    }
}
