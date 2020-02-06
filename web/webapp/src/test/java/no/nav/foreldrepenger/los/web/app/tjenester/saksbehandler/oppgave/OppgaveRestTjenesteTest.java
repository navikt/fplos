package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave;

import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.ReservasjonsEndringDto;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

public class OppgaveRestTjenesteTest {
    private static OppgaveRestTjeneste oppgaveRestTjeneste;
    private static OppgaveTjeneste oppgaveTjeneste;
    private static FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste;

    @BeforeClass
    public static void setUp() {
        oppgaveTjeneste = mock(OppgaveTjeneste.class);
        fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
        oppgaveRestTjeneste = new OppgaveRestTjeneste(oppgaveTjeneste,fagsakApplikasjonTjeneste);
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
        ReservasjonsEndringDto reservasjonsEndringDto = new ReservasjonsEndringDto(1L, LocalDate.now().plusDays(30));
        return reservasjonsEndringDto;
    }

    private ReservasjonsEndringDto lagReservasjonsEndringIntilIGår() {
        ReservasjonsEndringDto reservasjonsEndringDto = new ReservasjonsEndringDto(1L, LocalDate.now().minusDays(1));
        return reservasjonsEndringDto;
    }

    private ReservasjonsEndringDto lagReservasjonsEndring35DagerFrem() {
        ReservasjonsEndringDto reservasjonsEndringDto = new ReservasjonsEndringDto(1L, LocalDate.now().plusDays(35));
        return reservasjonsEndringDto;
    }
}
