package no.nav.fplos.uuid;

import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.uuid.repository.SpringOppgaveEventLoggRepository;
import no.nav.fplos.uuid.repository.SpringOppgaveRepository;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@PropertySource("classpath:spring-application.properties")
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
public class UUIDSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UUIDSyncService.class);
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
        List<Long> oppgaveBehandlinger = oppgaveRepository.finnBehandlingIdForOppgaverUtenEksternId();
        oppgaveBehandlinger.stream().forEach(behandlingId -> settUuidPåOppgaveFraBehandlingsId(behandlingId));

        List<Long> oppgaveEventBehandlinger = oppgaveEventLoggRepository.finnBehandlingIdForOppgaveEventerUtenEksternId();
        oppgaveEventBehandlinger.stream().forEach(behandlingId -> settUuidPåOppgaveEventFraBehandlingsId(behandlingId));

    }

    private void settUuidPåOppgaveFraBehandlingsId(Long behandlingId) {
        try {
            Optional<UUID> behandlingUUID = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingId);
            if(behandlingUUID.isPresent()) {
                oppgaveRepository.settInnUUIDForOppgaverMedBehandlingId(behandlingId, behandlingUUID.get());
                oppgaveEventLoggRepository.settInnUUIDForOppgaveEventerMedBehandlingId(behandlingId, behandlingUUID.get());
            } else {
                LOGGER.info("Fant ingen UUID for behandling med ID : " + behandlingId);
            }

        }catch(RuntimeException re){
            LOGGER.info("Feil ved kall til fpsak for behandling med ID : " + behandlingId + " . Feilmelding : " + re.getMessage());
        }

    }

    private void settUuidPåOppgaveEventFraBehandlingsId(Long behandlingId) {
        try {
            Optional<UUID> behandlingUUID = foreldrePengerBehandlingRestKlient.getBehandlingUUID(behandlingId);
            if(behandlingUUID.isPresent()) {
                oppgaveEventLoggRepository.settInnUUIDForOppgaveEventerMedBehandlingId(behandlingId, behandlingUUID.get());
            } else {
                LOGGER.info("Fant ingen UUID for behandling med ID : " + behandlingId);
            }

        }catch(RuntimeException re){
            LOGGER.info("Feil ved kall til fpsak for behandling med ID : " + behandlingId + " . Feilmelding : " + re.getMessage());
        }
    }
}
