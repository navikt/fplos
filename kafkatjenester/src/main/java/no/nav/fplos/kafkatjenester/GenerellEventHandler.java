package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.OppgaveEvent;
import no.nav.fplos.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

// tar inn, behandler og persisterer generelle events
@ApplicationScoped
@Transaction
public class GenerellEventHandler {

    private static final Logger log = LoggerFactory.getLogger(GenerellEventHandler.class);

    private KodeverkRepository kodeverkRepository;
    private OppgaveRepository oppgaveRepository;


    @Inject
    public GenerellEventHandler(KodeverkRepository kodeverkRepository, OppgaveRepository oppgaveRepository) {
        this.kodeverkRepository = kodeverkRepository;
        this.oppgaveRepository = oppgaveRepository;
    }

    public void prosesserMelding(String json) throws IOException {
        OppgaveEvent event = deserialize(json);
       // oppgaveRepository.lagre(event);

    }

    private OppgaveEvent deserialize(String json) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return mapper.readValue(json, OppgaveEvent.class);
        } catch (IOException e) {
            log.warn("Klarte ikke å deserialisere til " + OppgaveEvent.class, e);
            throw new IOException(e);
        }
    }
}
