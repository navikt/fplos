package no.nav.fplos.reservasjon;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;

import static no.nav.fplos.util.BrukerIdent.brukerIdent;

@ApplicationScoped
public class ReservasjonTjenesteImpl implements ReservasjonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ReservasjonTjenesteImpl.class);

    private OppgaveRepository oppgaveRepository;

    @Inject
    public ReservasjonTjenesteImpl(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    public ReservasjonTjenesteImpl() {
    }

    @Override
    public List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver() {
        return oppgaveRepository.hentReservasjonerTilknyttetAktiveOppgaver(brukerIdent());
    }

    @Override
    public List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet) {
        return oppgaveRepository.hentAlleReservasjonerForAvdeling(avdelingEnhet);
    }

    @Override
    public Reservasjon reserverOppgave(Long oppgaveId) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        if (!reservasjon.erAktiv()) {
            reservasjon.reserverNormalt();
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

    @Override
    public Reservasjon hentReservasjon(Long oppgaveId) {
        return oppgaveRepository.hentReservasjon(oppgaveId);
    }

    @Override
    public Reservasjon frigiOppgave(Long oppgaveId, String begrunnelse) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        Oppgave oppgave = reservasjon.getOppgave();
        reservasjon.frigiReservasjon(begrunnelse);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(oppgave);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    @Override
    public Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.flyttReservasjon(brukernavn, begrunnelse);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(reservasjon.getOppgave());
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    @Override
    public Reservasjon forlengReservasjonPåOppgave(Long oppgaveId) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.forlengReservasjonPåOppgave();
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    @Override
    public Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime reservertTil) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.endreReservasjonPåOppgave(reservertTil);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    @Override
    public List<Oppgave> hentSisteReserverteOppgaver() {
        return oppgaveRepository.hentSisteReserverteOppgaver(brukerIdent());
    }


}
