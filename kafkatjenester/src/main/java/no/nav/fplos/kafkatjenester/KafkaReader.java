package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.jsonoppgave.JsonOppgave;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.log.mdc.MDCOperations;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class KafkaReader {

    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private OppgaveRepository  oppgaveRepository;
    private FpsakEventHandler fpsakEventHandler;
    private TilbakekrevingEventHandler tilbakekrevingEventHandler;
    private AksjonspunktMeldingConsumer meldingConsumer;
    private StringBuilder feilmelding = new StringBuilder();
    private JsonOppgaveHandler jsonOppgaveHandler;
    private static final String CALLID_NAME = "Nav-CallId";

    public KafkaReader(){
        //to make proxyable
    }

    @Inject
    public KafkaReader(AksjonspunktMeldingConsumer meldingConsumer,
                       FpsakEventHandler fpsakEventHandler,
                       TilbakekrevingEventHandler tilbakekrevingEventHandler,
                       OppgaveRepository oppgaveRepository, JsonOppgaveHandler jsonOppgaveHandler){
        this.meldingConsumer = meldingConsumer;
        this.fpsakEventHandler = fpsakEventHandler;
        this.tilbakekrevingEventHandler = tilbakekrevingEventHandler;
        this.oppgaveRepository = oppgaveRepository;
        this.jsonOppgaveHandler = jsonOppgaveHandler;
    }

    public void hentOgLagreMeldingene() {
        ConsumerRecords<String, String> records = meldingConsumer.hentConsumerMeldingene();
        for (ConsumerRecord<String, String> record : records) {
            Headers headers = record.headers();
            for(Header header:headers){
                if(CALLID_NAME.equals(header.key())) {
                    String callId = new String(header.value());
                    MDCOperations.putCallId(callId);
                }
            }
            prosesser(record.value());
        }
        commitMelding();
    }

    private void commitMelding(){
        meldingConsumer.manualCommitSync();
    }

    public void prosesser(String melding) {
        log.info("Mottatt melding med start :" + melding.substring(0, Math.min(melding.length() - 1, 1000)));
        try {
            BehandlingProsessEventDto event = deserialiser(melding, BehandlingProsessEventDto.class);
            if (event != null) {
                switch (Fagsystem.valueOf(event.getFagsystem())) {
                    case FPSAK:
                        fpsakEventHandler.prosesser(event);
                        return;
                    case FPTILBAKE:
                        tilbakekrevingEventHandler.prosesser(event);
                        return;
                    default:
                        log.warn("BehandlingProsessEventDto har ikke gyldig verdi for fagsystem. Fagsystem {} er ikke støttet.",
                                event.getFagsystem());
                }
            }

            JsonOppgave oppgaveJson = deserialiser(melding, JsonOppgave.class);
            if (oppgaveJson != null) { jsonOppgaveHandler.prosesser(oppgaveJson); }

            lagreFeiletMelding(melding);
            log.warn("Kunne ikke deserialisere meldingen");
        } catch (Exception e) {
            log.warn("Behandling av event feilet. Lagret melding til EventMottakFeillogg for rekjøring.", e);
            feilmelding.append(e.getMessage());
            lagreFeiletMelding(melding);
        }
    }

    private <T> T deserialiser(String melding, Class<T> klassetype){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return mapper.readValue(melding, klassetype);
        } catch (IOException  e) {
            log.info("Klarte ikke å deserialisere basert på Objektet med klassetype " + klassetype + ", melding: " + melding, e);
            feilmelding.append(e.getMessage());
            return null;
        }
    }

    private void lagreFeiletMelding(String melding){
        oppgaveRepository.lagre(new EventmottakFeillogg(melding, feilmelding.toString()));
    }

}
