package no.nav.fplos.uuid;

import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.uuid.dao.Oppgave;
import no.nav.fplos.uuid.dao.OppgaveEventLogg;
import no.nav.fplos.uuid.repository.SpringOppgaveEventLoggRepository;
import no.nav.fplos.uuid.repository.SpringOppgaveRepository;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PropertySource("classpath:spring-application.properties")
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
public class UUIDSyncService {

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    private no.nav.fplos.uuid.repository.SpringOppgaveRepository oppgaveRepository;
    private no.nav.fplos.uuid.repository.SpringOppgaveEventLoggRepository oppgaveEventLoggRepository;
    @Autowired
    public UUIDSyncService(SpringOppgaveRepository oppgaveRepository, SpringOppgaveEventLoggRepository oppgaveEventLoggRepository, @Value("${fpsak.url}") String fpsakUrl) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEventLoggRepository = oppgaveEventLoggRepository;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OidcRestClient restClient = new OidcRestClient(httpClient);
        this.foreldrePengerBehandlingRestKlient = new ForeldrepengerBehandlingRestKlient(restClient,  fpsakUrl);
    }
    public void oppdaterUUID() {
        List<Oppgave> oppgaveBehandlinger = oppgaveRepository.finnOppgaverUtenEksternId();
        oppgaveBehandlinger.stream().forEach(oppgave -> settUuidPåOppgaveFraBehandlingsId(oppgave));

        List<OppgaveEventLogg> oppgaveEventBehandlinger = oppgaveEventLoggRepository.finnOppgaveEventerUtenEksternId();
        oppgaveEventBehandlinger.stream().forEach(oppgaveEvent -> settUuidPåOppgaveEventFraBehandlingsId(oppgaveEvent));
    }

    private void settUuidPåOppgaveFraBehandlingsId(Oppgave oppgave) {
        BehandlingFpsak sak = foreldrePengerBehandlingRestKlient.getBehandling(oppgave.getBehandlingId());
        if(sak != null) {
            oppgave.setEksternId(sak.getUuid());
            oppgaveRepository.save(oppgave);
        }
    }

    private void settUuidPåOppgaveEventFraBehandlingsId(OppgaveEventLogg oppgaveEvent) {
        BehandlingFpsak sak = foreldrePengerBehandlingRestKlient.getBehandling(oppgaveEvent.getBehandlingId());
        if(sak != null) {
            oppgaveEvent.setEksternId(sak.getUuid());
            oppgaveEventLoggRepository.save(oppgaveEvent);
        }
    }
}
