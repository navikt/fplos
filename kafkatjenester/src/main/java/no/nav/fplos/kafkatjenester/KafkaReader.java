package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakStatus;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.kafkatjenester.jsonoppgave.JsonOppgave;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class KafkaReader {

    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private OppgaveRepository  oppgaveRepository;
    private FpsakEventHandler fpsakEventHandler;
    private JsonOppgaveHandler jsonOppgaveHandler;
    private AksjonspunktMeldingConsumer meldingConsumer;
    private StringBuilder feilmelding;

    public KafkaReader(){
        //to make proxyable
    }

    @Inject
    public KafkaReader(AksjonspunktMeldingConsumer meldingConsumer,
                       JsonOppgaveHandler jsonOppgaveHandler, FpsakEventHandler fpsakEventHandler,
                       OppgaveRepositoryProvider oppgaveRepositoryProvider){
        this.meldingConsumer = meldingConsumer;
        this.jsonOppgaveHandler = jsonOppgaveHandler;
        this.fpsakEventHandler = fpsakEventHandler;
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
    }

    public void hentOgLagreMeldingene() {
        List<String> meldinger = meldingConsumer.hentConsumerMeldingene();
        for (String melding : meldinger) {
            prosesser(melding);
        }
        commitMelding();
    }

    private void commitMelding(){
        meldingConsumer.manualCommitSync();
    }

    public void prosesser(String melding) {
        log.info("Mottatt melding med start :" + melding.substring(0, Math.min(melding.length() - 1, 1000)));
        feilmelding = new StringBuilder();
        try {
            BehandlingProsessEventDto behandlingProsessEventDto = deserialiser(melding, BehandlingProsessEventDto.class);
            if (behandlingProsessEventDto != null) {
                fpsakEventHandler.prosesser(behandlingProsessEventDto);
                return;
            }
            JsonOppgave jsonOppgave = deserialiser(melding, JsonOppgave.class);
            if (jsonOppgave != null) {
                log.info("Kaller JsonOppgaveHandler"); // antar JsonOppgave er legacy for de eldste meldingene
                jsonOppgaveHandler.prosesser(jsonOppgave);
                return;
            }
            loggFeiletDeserialisering(melding);
            log.error("Klarte ikke å deserialisere meldingen");
        } catch (Exception tekniskException) {
            feilmelding.append(tekniskException.getMessage());
            log.warn("Feil ved deserialisering lagt til i logg", tekniskException);
            loggFeiletDeserialisering(melding);
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

    private void loggFeiletDeserialisering(String melding){
        oppgaveRepository.lagre(new EventmottakFeillogg(melding, EventmottakStatus.FEILET, LocalDateTime.now(), feilmelding.toString()));
    }

}
