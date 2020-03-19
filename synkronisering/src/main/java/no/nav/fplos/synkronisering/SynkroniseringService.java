package no.nav.fplos.synkronisering;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.synkronisering.repository.SpringOppgaveRepository;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@PropertySource("classpath:spring-application.properties")
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
public class SynkroniseringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SynkroniseringService.class);
    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;
    private SpringOppgaveRepository oppgaveRepository;

    @Autowired
    public SynkroniseringService(SpringOppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OidcRestClient restClient = new OidcRestClient(httpClient);
        this.foreldrePengerBehandlingRestKlient = new ForeldrepengerBehandlingRestKlient(restClient, null);
    }

    public void oppdater() {
        List<UUID> oppgaveBehandlinger = oppgaveRepository.finnBehandlingerUtenFelter();
        oppgaveBehandlinger.forEach(this::oppdaterManglendeFørsteuttaksdagOgFrist);
    }

    private void oppdaterManglendeFørsteuttaksdagOgFrist(UUID behandlingId) {
        try {
            BehandlingFpsak behandlingFpsak = foreldrePengerBehandlingRestKlient.getBehandling(new BehandlingId(behandlingId));
            oppgaveRepository.oppdaterBehandlingsfristOgFørstestønadsdag(behandlingId, behandlingFpsak.getFørsteUttaksdag(), behandlingFpsak.getBehandlingstidFrist());
        } catch (RuntimeException re) {
            LOGGER.info("Feil ved kall til fpsak for : " + behandlingId + " . Feilmelding : " + re.getMessage());
        }
    }
}
