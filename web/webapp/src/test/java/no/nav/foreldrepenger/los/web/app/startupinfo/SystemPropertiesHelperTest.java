package no.nav.foreldrepenger.los.web.app.startupinfo;

import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemPropertiesHelperTest {

    private SystemPropertiesHelper helper; // objektet som testes

    @Before
    public void setup() {
        helper = SystemPropertiesHelper.getInstance();
    }

    @Test
    public void test_sysProps() {
        SortedMap<String, String> sysProps = helper.filteredSortedProperties();

        assertThat(sysProps).isNotNull();
        assertThat(sysProps.get("java.version")).isNotNull();
    }

    @Test
    public void test_envVars() {
        SortedMap<String, String> envVars = helper.filteredSortedEnvVars();

        assertThat(envVars).isNotNull();
        assertThat(envVars.size()).isGreaterThanOrEqualTo(1);
    }
}
