package no.nav.foreldrepenger.los.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private KafkaConsumerStarter kafkaConsumerStarter = mock(KafkaConsumerStarter.class);

    @Before
    public void setup() {
        restTjeneste = new NaisRestTjeneste(kafkaConsumerStarter);
    }

    @Test
    public void test_isAlive_skal_returnere_status_200() {
        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test_preStop_skal_kalle_stopServices_og_returnere_status_ok() {
        Response response = restTjeneste.preStop();

        verify(kafkaConsumerStarter).destroy();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
