package no.nav.fplos.batch;

import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class UUIDSync {

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    private SpringOppgaveRepositoryInterface repo;
    @Autowired
    public UUIDSync(SpringOppgaveRepositoryInterface repo) {
        this.repo = repo;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OidcRestClient restClient = new OidcRestClient(httpClient);
        this.foreldrePengerBehandlingRestKlient = new ForeldrepengerBehandlingRestKlient(restClient,  "http://localhost:8080");
    }

    //@Scheduled(cron = "0 15 06 * * ?")
    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        oppdaterUUID();
    }

    private void oppdaterUUID() {
        //List<Oppgave> oppgaver = (List<Oppgave>) repo.findAll();

        Long behandlingId = 1000101L;
        finnUUIDFraBehandlingsId(behandlingId);
    }

    private void finnUUIDFraBehandlingsId(Long behandlingId) {
        BehandlingFpsak sak = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        System.out.println(sak);
    }


}
