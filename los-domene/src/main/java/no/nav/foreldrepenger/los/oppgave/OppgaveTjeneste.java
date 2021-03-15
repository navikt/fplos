package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

@ApplicationScoped
public class OppgaveTjeneste {

    private OppgaveRepository oppgaveRepository;

    @Inject
    public OppgaveTjeneste(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    OppgaveTjeneste() {
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(fagsakSaksnummerListe);
    }

    public boolean erAlleOppgaverFortsattTilgjengelig(List<Long> oppgaveIder) {
        var oppgaver = oppgaveRepository.sjekkOmOppgaverFortsattErTilgjengelige(oppgaveIder);
        return oppgaver.size() == oppgaveIder.size();
    }

    public Oppgave hentOppgave(Long oppgaveId) {
        return oppgaveRepository.hentOppgave(oppgaveId);
    }

    public Optional<Oppgave> hentNyesteOppgaveTilknyttet(BehandlingId behandlingId) {
        return oppgaveRepository.hentOppgaver(behandlingId).stream()
                .min((o1, o2) -> aktuellDato(o2).compareTo(aktuellDato(o1)));
    }

    private static LocalDateTime aktuellDato(Oppgave oppgave) {
        return oppgave.getOppgaveAvsluttet() == null ? oppgave.getOpprettetTidspunkt() : oppgave.getOppgaveAvsluttet();
    }
}
