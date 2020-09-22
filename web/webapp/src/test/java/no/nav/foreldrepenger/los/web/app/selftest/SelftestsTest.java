package no.nav.foreldrepenger.los.web.app.selftest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

import no.nav.foreldrepenger.los.web.app.selftest.checks.ExtHealthCheck;

@EnableWeld
public class SelftestsTest {

    @Inject
    @Any
    Instance<ExtHealthCheck> healthChecks;

    private Selftests selftests;

    @BeforeEach
    public void setup() {
        HealthCheckRegistry registry = Mockito.mock(HealthCheckRegistry.class);

        List<ExtHealthCheck> checks = new ArrayList<>();

        for(ExtHealthCheck ex: healthChecks){
            ExtHealthCheck newEx = Mockito.spy(ex);
            Mockito.doReturn(false).when(newEx).erKritiskTjeneste();
            checks.add(newEx);
        }

        @SuppressWarnings("unchecked")
        Instance<ExtHealthCheck> testInstance = Mockito.mock(Instance.class);
        Mockito.doReturn(checks.iterator()).when(testInstance).iterator();
        selftests = new Selftests(registry, testInstance, "fplos");
    }

    @Test
    public void test_run_skal_utfoere_alle_del_tester() {
        SelftestResultat samletResultat = selftests.run();

        assertThat(samletResultat != null).isTrue();
        assertThat(samletResultat.getApplication()).isNotNull();
        assertThat(samletResultat.getVersion()).isNotNull();
        assertThat(samletResultat.getTimestamp()).isNotNull();
        assertThat(samletResultat.getRevision()).isNotNull();
        assertThat(samletResultat.getBuildTime()).isNotNull();
        assertThat(samletResultat.getAggregateResult()).isNotNull();
        List<HealthCheck.Result> resultList = samletResultat.getAlleResultater();
        assertThat(resultList).isNotNull();
    }
}
