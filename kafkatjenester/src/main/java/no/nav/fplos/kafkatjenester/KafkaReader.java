package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.kafkatjenester.jsonoppgave.JsonOppgave;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class KafkaReader {

    private static final Logger log = LoggerFactory.getLogger(KafkaReader.class);
    private OppgaveRepository  oppgaveRepository;
    private FpsakEventHandler fpsakEventHandler;
    private TilbakekrevingEventHandler tilbakekrevingEventHandler;
    private AksjonspunktMeldingConsumer meldingConsumer;
    private StringBuilder feilmelding = new StringBuilder();
    private JsonOppgaveHandler jsonOppgaveHandler;

    public KafkaReader(){
        //to make proxyable
    }

    @Inject
    public KafkaReader(AksjonspunktMeldingConsumer meldingConsumer,
                       FpsakEventHandler fpsakEventHandler,
                       TilbakekrevingEventHandler tilbakekrevingEventHandler,
                       OppgaveRepositoryProvider oppgaveRepositoryProvider, JsonOppgaveHandler jsonOppgaveHandler){
        this.meldingConsumer = meldingConsumer;
        this.fpsakEventHandler = fpsakEventHandler;
        this.tilbakekrevingEventHandler = tilbakekrevingEventHandler;
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        this.jsonOppgaveHandler = jsonOppgaveHandler;
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
