package no.nav.foreldrepenger.los.reservasjon;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservasjonTidspunktUtilTest {

    @Test
    void testEndreOppgaveReservasjonFeilerUtenforPeriode() {
        var date = LocalDate.now().plusDays(35);
        assertThrows(IllegalArgumentException.class, () -> ReservasjonTidspunktUtil.utledReservasjonTidspunkt(date));
    }

    @Test
    void testEndreOppgaveReservasjonFeilerTilbakeITid() {
        var date = LocalDate.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> ReservasjonTidspunktUtil.utledReservasjonTidspunkt(date));
    }

    @Test
    void testEndreOppgaveReservasjonOK() {
        var localDateTime = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().plusDays(30));
        assertThat(localDateTime.getHour()).isEqualTo(23);
        assertThat(localDateTime.getMinute()).isEqualTo(59);
    }

}
