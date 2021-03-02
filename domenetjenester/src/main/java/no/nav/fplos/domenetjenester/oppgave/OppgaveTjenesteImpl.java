package no.nav.fplos.domenetjenester.oppgave;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OppgaveTjenesteImpl implements OppgaveTjeneste {

    private OppgaveRepository oppgaveRepository;

    @Inject
    public OppgaveTjenesteImpl(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    OppgaveTjenesteImpl() {
    }

    @Override
    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(fagsakSaksnummerListe);
    }

    public boolean erAlleOppgaverFortsattTilgjengelig(List<Long> oppgaveIder) {
        List<Oppgave> oppgaver = oppgaveRepository.sjekkOmOppgaverFortsattErTilgjengelige(oppgaveIder);
        return oppgaver.size() == oppgaveIder.size();
    }

    @Override
    public Oppgave hentOppgave(Long oppgaveId) {
        return oppgaveRepository.hentOppgave(oppgaveId);
    }

    @Override
    public Optional<Oppgave> hentNyesteOppgaveTilknyttet(BehandlingId behandlingId) {
        return oppgaveRepository.hentOppgaver(behandlingId).stream()
                .min((o1, o2) -> aktuellDato(o2).compareTo(aktuellDato(o1)));
    }

    private static LocalDateTime aktuellDato(Oppgave oppgave) {
        return oppgave.getOppgaveAvsluttet() == null ? oppgave.getOpprettetTidspunkt() : oppgave.getOppgaveAvsluttet();
    }
}
