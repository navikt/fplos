package no.nav.foreldrepenger.los.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.web.app.selftest.Selftests;

public class HealthCheckRestServiceTest {

    private HealthCheckRestService restTjeneste;

    private final Selftests selftests = mock(Selftests.class);

    @BeforeEach
    public void setup() {
        restTjeneste = new HealthCheckRestService(selftests);
    }

    @Test
    public void isAlive_skal_returnere_status_200() {
        when(selftests.isKafkaAlive()).thenReturn(true);
        when(selftests.isReady()).thenReturn(true);

        var response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_service_unavailable_når_selftester_feiler() {
        when(selftests.isKafkaAlive()).thenReturn(false);
        when(selftests.isReady()).thenReturn(false);

        var responseReady = restTjeneste.isReady();

        var responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_delvis_når_db_feiler() {
        when(selftests.isKafkaAlive()).thenReturn(true);
        when(selftests.isReady()).thenReturn(false);

        var responseReady = restTjeneste.isReady();
        var responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void isReady_skal_returnere_status_ok_når_selftester_er_ok() {
        when(selftests.isKafkaAlive()).thenReturn(true);
        when(selftests.isReady()).thenReturn(true);

        var responseReady = restTjeneste.isReady();
        var responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }
}
