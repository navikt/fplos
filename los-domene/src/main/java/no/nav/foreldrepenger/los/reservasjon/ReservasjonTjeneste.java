package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;

import static no.nav.foreldrepenger.los.felles.util.BrukerIdent.brukerIdent;


@ApplicationScoped
public class ReservasjonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ReservasjonTjeneste.class);

    private OppgaveRepository oppgaveRepository;
    private ReservasjonRepository reservasjonRepository;

    @Inject
    public ReservasjonTjeneste(OppgaveRepository oppgaveRepository, ReservasjonRepository reservasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.reservasjonRepository = reservasjonRepository;
    }

    public ReservasjonTjeneste() {
    }

    public List<Oppgave> hentSaksbehandlersReserverteAktiveOppgaver() {
        return reservasjonRepository.hentSaksbehandlersReserverteAktiveOppgaver(brukerIdent());
    }

    public List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet) {
        return reservasjonRepository.hentAlleReservasjonerForAvdeling(avdelingEnhet);
    }

    public Reservasjon reserverOppgave(Long oppgaveId) {
        return reserverOppgave(oppgaveRepository.hentOppgave(oppgaveId));
    }

    public Reservasjon reserverOppgave(Oppgave oppgave) {
        var reservasjon = oppgaveRepository.hentReservasjon(oppgave.getId())
                .map((r -> {
                    r.setFlyttetTidspunkt(null);
                    r.setBegrunnelse(null);
                    r.setFlyttetAv(null);
                    return r;
                })).orElseGet(() -> new Reservasjon(oppgave));
        if (!reservasjon.erAktiv()) {
            reservasjon.setReservertTil(standardReservasjon());
            reservasjon.setReservertAv(brukerIdent());
            try {
                oppgaveRepository.lagre(reservasjon);
                oppgaveRepository.refresh(reservasjon.getOppgave());
                oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
            } catch (PersistenceException e) {
                // ignorerer feil ettersom ReservasjonDto til frontend vil vise at reservasjon tilhører annen
                LOG.info("Antatt kollisjon på reservasjon", e);
                oppgaveRepository.refresh(reservasjon.getOppgave());
            }
        }
        return reservasjon;
    }

    public Reservasjon hentReservasjon(Long oppgaveId) {
        return oppgaveRepository.hentReservasjon(oppgaveId).orElse(null);
    }

    public Optional<Reservasjon> slettReservasjonMedEventLogg(Long oppgaveId, String begrunnelse) {
        var reservasjon = reservasjonRepository.hentAktivReservasjon(oppgaveId);
        reservasjon.ifPresentOrElse(res -> slettReservasjonMedEventLogg(res, begrunnelse),
                () -> LOG.info("Forsøker slette reservasjon, men fant ingen for oppgaveId {}", oppgaveId)
        );
        return reservasjon;
    }

    public void slettReservasjonMedEventLogg(Reservasjon reservasjon, String begrunnelse) {
        if (reservasjon != null) {
            reservasjon.setReservertTil(LocalDateTime.now().minusSeconds(1));
            reservasjon.setFlyttetAv(null);
            reservasjon.setFlyttetTidspunkt(null);
            reservasjon.setBegrunnelse(begrunnelse);
            reservasjonRepository.lagre(reservasjon);
            reservasjonRepository.lagre(new ReservasjonEventLogg(reservasjon));
        }
    }

    public Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse) {
        return oppgaveRepository.hentReservasjon(oppgaveId)
                .map(res -> {
                    res.setReservertTil(res.getReservertTil().plusHours(24));
                    res.setReservertAv(brukernavn);
                    res.setFlyttetAv(brukerIdent());
                    res.setFlyttetTidspunkt(LocalDateTime.now());
                    res.setBegrunnelse(begrunnelse);
                    oppgaveRepository.lagre(res);
                    oppgaveRepository.refresh(res.getOppgave());
                    oppgaveRepository.lagre(new ReservasjonEventLogg(res));
                    return res;
                })
                .orElseThrow(() -> new IllegalStateException("Fant ikke reservasjon tilknyttet oppgaveId " + oppgaveId));
    }

    public Reservasjon forlengReservasjonPåOppgave(Long oppgaveId) {
        var reservasjon = oppgaveRepository.hentReservasjon(oppgaveId)
                .orElseThrow(() -> new IllegalStateException("Fant ikke reservasjon tilknyttet oppgaveId " + oppgaveId));
        reservasjon.setReservertTil(utvidetReservasjon(reservasjon.getReservertTil()));
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    public Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime reservertTil) {
        var reservasjon = oppgaveRepository.hentReservasjon(oppgaveId)
                .orElseThrow(() -> new IllegalStateException("Fant ikke reservasjon tilknyttet oppgaveId " + oppgaveId));
        reservasjon.setReservertTil(reservertTil);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    public List<Oppgave> hentSaksbehandlersSisteReserverteOppgaver() {
        return reservasjonRepository.hentSaksbehandlersSisteReserverteOppgaver(brukerIdent());
    }

    private static LocalDateTime standardReservasjon() {
        return LocalDateTime.now().plusHours(8);
    }

    private static LocalDateTime utvidetReservasjon(LocalDateTime eksisterende) {
        return eksisterende.plusHours(24);
    }

    public void opprettReservasjon(Oppgave oppgave, String saksbehandler, String begrunnelse) {
        var reservertTil = LocalDateTime.now().plusHours(24);
        var reservasjon = new Reservasjon(oppgave);
        reservasjon.setReservertAv(saksbehandler);
        reservasjon.setBegrunnelse(begrunnelse);
        reservasjon.setReservertTil(reservertTil);
        reservasjon.setFlyttetAv(brukerIdent());
        reservasjon.setFlyttetTidspunkt(LocalDateTime.now());
        reservasjonRepository.lagre(reservasjon);
        var rel = new ReservasjonEventLogg(reservasjon);
        reservasjonRepository.lagre(rel);
    }
}
