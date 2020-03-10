package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.ReservasjonsEndringDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;

public class OppgaveRestTjenesteTest {
    private static OppgaveRestTjeneste oppgaveRestTjeneste;

    @BeforeClass
    public static void setUp() {
        OppgaveTjeneste oppgaveTjeneste = mock(OppgaveTjeneste.class);
        FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
        TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
        oppgaveRestTjeneste = new OppgaveRestTjeneste(oppgaveTjeneste, fagsakApplikasjonTjeneste, tpsTjeneste);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEndreOppgaveReservasjonFeilerUtenforPeriode(){
        oppgaveRestTjeneste.endreOppgaveReservasjon(lagReservasjonsEndring35DagerFrem());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEndreOppgaveReservasjonFeilerTilbakeITid(){
        oppgaveRestTjeneste.endreOppgaveReservasjon(lagReservasjonsEndringIntilIGår());
    }

    @Test
    public void testEndreOppgaveReservasjonOK(){
        oppgaveRestTjeneste.endreOppgaveReservasjon(lagReservasjonsEndring30DagerFrem());
    }

    private ReservasjonsEndringDto lagReservasjonsEndring30DagerFrem() {
        return new ReservasjonsEndringDto(1L, LocalDate.now().plusDays(30));
    }

    private ReservasjonsEndringDto lagReservasjonsEndringIntilIGår() {
        return new ReservasjonsEndringDto(1L, LocalDate.now().minusDays(1));
    }

    private ReservasjonsEndringDto lagReservasjonsEndring35DagerFrem() {
        return new ReservasjonsEndringDto(1L, LocalDate.now().plusDays(35));
    }
}
