package no.nav.foreldrepenger.los.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.los.web.app.selftest.checks.DatabaseHealthCheck;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private KafkaConsumerStarter kafkaConsumerStarter = mock(KafkaConsumerStarter.class);
    private DatabaseHealthCheck databaseHealthCheck = mock(DatabaseHealthCheck.class);

    @Before
    public void setup() {
        restTjeneste = new NaisRestTjeneste(kafkaConsumerStarter, databaseHealthCheck);
    }

    @Test
    public void isAlive_skal_returnere_status_200_hvis_kafka_ok_db_ok() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(true);
        when(databaseHealthCheck.isReady()).thenReturn(true);

        assertThat(restTjeneste.isAlive().getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isAlive_skal_returnere_status_200_hvis_kafka_ok_db_feil() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(true);
        when(databaseHealthCheck.isReady()).thenReturn(false);

        assertThat(restTjeneste.isAlive().getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isAlive_skal_returnere_status_500_hvis_kafka_feil_db_ok() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(false);
        when(databaseHealthCheck.isReady()).thenReturn(true);

        assertThat(restTjeneste.isAlive().getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_200_hvis_kafka_ok_db_ok() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(true);
        when(databaseHealthCheck.isReady()).thenReturn(true);

        assertThat(restTjeneste.isReady().getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_503_hvis_kafka_ok_db_feil() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(true);
        when(databaseHealthCheck.isReady()).thenReturn(false);

        assertThat(restTjeneste.isReady().getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_503_hvis_kafka_feil_db_ok() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(false);
        when(databaseHealthCheck.isReady()).thenReturn(true);

        assertThat(restTjeneste.isReady().getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_503_hvis_kafka_feil_db_feil() {
        when(kafkaConsumerStarter.isConsumersRunning()).thenReturn(false);
        when(databaseHealthCheck.isReady()).thenReturn(false);

        assertThat(restTjeneste.isReady().getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void preStop_skal_kalle_stopServices_og_returnere_status_ok() {
        Response response = restTjeneste.preStop();

        verify(kafkaConsumerStarter).destroy();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
