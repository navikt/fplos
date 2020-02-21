package no.nav.fplos.oppgave;

import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OppgaveTjeneste {

    List<Oppgave> hentOppgaver(Long sakslisteId);

    List<Oppgave> hentNesteOppgaver(Long sakslisteId);

    List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer);

    List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe);

    List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver();

    Reservasjon reserverOppgave(Long oppgaveId);

    Reservasjon hentReservasjon(Long oppgaveId);

    Reservasjon frigiOppgave(Long oppgaveId, String begrunnelse);

    Reservasjon forlengReservasjonPåOppgave(Long oppgaveId);

    Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime forlengTil);

    Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse);

    List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet);

    List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent);

    List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker();

    TpsPersonDto hentPersonInfo(long aktørId);

    Optional<TpsPersonDto> hentPersonInfoOptional(long aktørId);

    Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder);

    Integer hentAntallOppgaverForAvdeling(String avdelingEnhet);

    boolean harForandretOppgaver(List<Long> oppgaveIder);

    List<SaksbehandlerinformasjonDto> hentSakslistensSaksbehandlere(Long sakslisteId);

    List<Oppgave> hentSisteReserverteOppgaver();

    SaksbehandlerinformasjonDto hentSaksbehandlerNavnOgAvdelinger(String ident);

    String hentNavnHvisReservertAvAnnenSaksbehandler(Reservasjon reservasjon);

    String hentNavnHvisFlyttetAvSaksbehandler(String flyttetAv);
}
