package no.nav.fplos.domenetjenester.reservasjon;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservasjonTjeneste {
    List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver();

    Reservasjon reserverOppgave(Long oppgaveId);

    Reservasjon hentReservasjon(Long oppgaveId);

    Reservasjon frigiOppgave(Long oppgaveId, String begrunnelse);

    Reservasjon forlengReservasjonPåOppgave(Long oppgaveId);

    Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime forlengTil);

    Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse);

    List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet);

    List<Oppgave> hentSisteReserverteOppgaver();
}
