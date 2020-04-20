package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Test;

public class ReservasjonTidspunktUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEndreOppgaveReservasjonFeilerUtenforPeriode(){
        ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().plusDays(35));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEndreOppgaveReservasjonFeilerTilbakeITid(){
        ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().minusDays(1));
    }

    @Test
    public void testEndreOppgaveReservasjonOK(){
        var localDateTime = ReservasjonTidspunktUtil.utledReservasjonTidspunkt(LocalDate.now().plusDays(30));
        assertThat(localDateTime.getHour()).isEqualTo(23);
        assertThat(localDateTime.getMinute()).isEqualTo(59);
    }

}
