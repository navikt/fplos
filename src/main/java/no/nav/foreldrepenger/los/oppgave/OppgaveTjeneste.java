package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;

@ApplicationScoped
public class OppgaveTjeneste {

    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveRepository oppgaveRepository;

    @Inject
    public OppgaveTjeneste(OppgaveRepository oppgaveRepository, ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }

    OppgaveTjeneste() {
    }

    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Saksnummer> saksnummerListe) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(saksnummerListe);
    }

    public boolean erAlleOppgaverFortsattTilgjengelig(List<Long> oppgaveIder) {
        return oppgaveRepository.sjekkOmOppgaverFortsattErTilgjengelige(oppgaveIder);
    }

    public Oppgave hentOppgave(Long oppgaveId) {
        return oppgaveRepository.hentOppgave(oppgaveId);
    }

    public Optional<Oppgave> hentNyesteOppgaveTilknyttet(BehandlingId behandlingId) {
        return oppgaveRepository.hentOppgaver(behandlingId).stream().min((o1, o2) -> aktuellDato(o2).compareTo(aktuellDato(o1)));
    }

    public Optional<Oppgave> hentAktivOppgave(BehandlingId behandlingId) {
        return oppgaveRepository.hentAktivOppgave(behandlingId);
    }

    public void avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(BehandlingId behandlingId) {
        var oppgaver = oppgaveRepository.hentOppgaver(behandlingId);
        var antallAktive = oppgaver.stream().filter(Oppgave::getAktiv).count();
        if (antallAktive > 1) {
            throw new IllegalStateException(String.format("Forventet kun én aktiv oppgave for behandlingId %s, fant %s", behandlingId, antallAktive));
        }
        oppgaver.stream().filter(Oppgave::getAktiv).max(Comparator.comparing(Oppgave::getOpprettetTidspunkt)).ifPresent(oppgave -> {
            reservasjonTjeneste.slettReservasjon(oppgave.getReservasjon());
            oppgave.avsluttOppgave();
            oppgaveRepository.lagre(oppgave);
            oppgaveRepository.refresh(oppgave);
        });
    }

    public void adminAvsluttMultiOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(BehandlingId behandlingId) {
        var oppgaver = oppgaveRepository.hentOppgaver(behandlingId);
        var antallAktive = oppgaver.stream().filter(Oppgave::getAktiv).count();
        if (antallAktive <= 1) {
            throw new IllegalStateException(
                String.format("Forventet mer enn én aktiv oppgave for behandlingId %s, fant %s", behandlingId, antallAktive));
        }
        oppgaver.stream().filter(Oppgave::getAktiv).min(Comparator.comparing(Oppgave::getOpprettetTidspunkt)).ifPresent(oppgave -> {
            reservasjonTjeneste.slettReservasjon(oppgave.getReservasjon());
            oppgave.avsluttOppgave();
            oppgaveRepository.lagre(oppgave);
            oppgaveRepository.refresh(oppgave);
        });
    }

    public void avsluttOppgaveMedEventLogg(Oppgave oppgave, OppgaveEventType oppgaveEventType) {
        oppgave.avsluttOppgave();
        reservasjonTjeneste.slettReservasjon(oppgave.getReservasjon());
        oppgaveRepository.lagre(oppgave);
        var oel = new OppgaveEventLogg.Builder().behandlingId(oppgave.getBehandlingId())
            .behandlendeEnhet(oppgave.getBehandlendeEnhet())
            .type(oppgaveEventType)
            .build();
        oppgaveRepository.lagre(oel);
    }

    public Oppgave gjenåpneTilbakekrevingOppgave(BehandlingId behandlingId) {
        var oppgaver = oppgaveRepository.hentOppgaver(behandlingId);
        var sisteOppgave = oppgaver.stream().max(Comparator.comparing(Oppgave::getOpprettetTidspunkt)).orElseThrow();
        if (sisteOppgave.getSystem() != Fagsystem.FPTILBAKE) {
            throw new IllegalStateException("Skal ikke gjenåpne oppgave for fpsak-behandlinger");
        }
        sisteOppgave.gjenåpneOppgave();
        oppgaveRepository.lagre(sisteOppgave);
        oppgaveRepository.refresh(sisteOppgave);
        return sisteOppgave;
    }

    public Optional<Oppgave> hentAktivTilbakekrevingOppgave(BehandlingId behandlingId) {
        return oppgaveRepository.hentOppgaver(behandlingId).stream().filter(Oppgave::getAktiv).findFirst();
    }

    public <U extends BaseEntitet> void lagre(U entitet) {
        oppgaveRepository.lagre(entitet);
    }

    private static LocalDateTime aktuellDato(Oppgave oppgave) {
        return oppgave.getOppgaveAvsluttet() == null ? oppgave.getOpprettetTidspunkt() : oppgave.getOppgaveAvsluttet();
    }

}
