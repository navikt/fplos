package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ReservasjonTidspunktUtilTest {

    @Test
    public void testEndreOppgaveReservasjonFeilerUtenforPeriode(){
        assertThrows(IllegalArgumentException.class, () -> ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().plusDays(35)));
    }

    @Test
    public void testEndreOppgaveReservasjonFeilerTilbakeITid(){
        assertThrows(IllegalArgumentException.class, () -> ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().minusDays(1)));
    }

    @Test
    public void testEndreOppgaveReservasjonOK(){
        var localDateTime = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().plusDays(30));
        assertThat(localDateTime.getHour()).isEqualTo(23);
        assertThat(localDateTime.getMinute()).isEqualTo(59);
    }

}
