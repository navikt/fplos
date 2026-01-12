package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import no.nav.foreldrepenger.los.felles.util.BrukerIdent;
import no.nav.foreldrepenger.los.felles.util.DateAndTimeUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.tjenester.felles.dto.OppgaveBehandlingStatus;


@ApplicationScoped
public class ReservasjonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ReservasjonTjeneste.class);
    private static final String FANT_IKKE_RESERVASJON_TILKNYTTET_OPPGAVE_ID = "Fant ikke reservasjon tilknyttet oppgaveId ";

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
        return reservasjonRepository.hentSaksbehandlersReserverteAktiveOppgaver(BrukerIdent.brukerIdent());
    }

    public List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet) {
        return reservasjonRepository.hentAlleReservasjonerForAvdeling(avdelingEnhet);
    }

    public Reservasjon reserverOppgave(Long oppgaveId) {
        return reserverOppgave(oppgaveRepository.hentOppgave(oppgaveId));
    }

    public Reservasjon reserverOppgave(Oppgave oppgave) {
        return reserverOppgave(oppgave, BrukerIdent.brukerIdent());
    }

    public Reservasjon reserverOppgave(Oppgave oppgave, String reservertAv) {
        LOG.info("Reserverer oppgave {}", oppgave.getId());
        var reservasjon = oppgaveRepository.hentReservasjon(oppgave.getId()).map((r -> {
            r.setFlyttetTidspunkt(null);
            r.setBegrunnelse(null);
            r.setFlyttetAv(null);
            return r;
        })).orElseGet(() -> new Reservasjon(oppgave));
        if (reservasjon.erAktiv()) {
            LOG.info("Fant aktiv reservasjon for oppgave {} reservasjon {}", oppgave.getId(), reservasjon.getId());
        } else {
            LOG.info("Fant ikke aktiv reservasjon for oppgave {}", oppgave.getId());
            reservasjon.setReservertTil(standardReservasjon());
            reservasjon.setReservertAv(reservertAv);
            try {
                oppgaveRepository.lagre(reservasjon);
                oppgaveRepository.refresh(reservasjon.getOppgave());
            } catch (OptimisticLockException e) {
                // Annen saksbehandler har oppdatert reservasjonen – refresher reservasjonen og returnerer den som om reservasjon ellers var vellykket
                // Frontend vil vise modal om at annen saksbehandler holder reservasjonen.
                oppgaveRepository.refresh(reservasjon);
                LOG.info("Reservasjon feilet: annen saksbehandler {} har oppdatert reservasjon", reservasjon.getReservertAv());
            } catch (PersistenceException e) {
                // Sannsynligvis har annen saksbehandler laget ny reservasjon.
                // Ignorerer feil ettersom ReservasjonDto til frontend vil vise modal
                oppgaveRepository.refresh(oppgave);
                LOG.info("Reservasjon feilet", e);
            }
        }
        return reservasjon;
    }

    public Optional<Reservasjon> slettReservasjon(Long oppgaveId) {
        var reservasjon = reservasjonRepository.hentAktivReservasjon(oppgaveId);
        reservasjon.ifPresentOrElse(this::slettReservasjon,
            () -> LOG.info("Forsøker slette reservasjon, men fant ingen for oppgaveId {}", oppgaveId));
        return reservasjon;
    }

    public void slettReservasjon(Reservasjon reservasjon) {
        if (reservasjon != null && reservasjon.erAktiv()) {
            reservasjon.setReservertTil(LocalDateTime.now().minusSeconds(1));
            reservasjonRepository.lagre(reservasjon);
        }
    }

    public Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse) {
        return oppgaveRepository.hentReservasjon(oppgaveId).map(res -> {
            res.setReservertTil(res.getReservertTil().plusHours(24).with(DateAndTimeUtil.justerTilNesteUkedag));
            res.setReservertAv(brukernavn);
            res.setFlyttetAv(BrukerIdent.brukerIdent());
            res.setFlyttetTidspunkt(LocalDateTime.now());
            res.setBegrunnelse(begrunnelse);
            oppgaveRepository.lagre(res);
            oppgaveRepository.refresh(res.getOppgave());
            return res;
        }).orElseThrow(() -> new IllegalStateException(FANT_IKKE_RESERVASJON_TILKNYTTET_OPPGAVE_ID + oppgaveId));
    }

    public Reservasjon forlengReservasjonPåOppgave(Long oppgaveId) {
        var reservasjon = oppgaveRepository.hentReservasjon(oppgaveId)
            .orElseThrow(() -> new IllegalStateException(FANT_IKKE_RESERVASJON_TILKNYTTET_OPPGAVE_ID + oppgaveId));
        reservasjon.setReservertTil(utvidetReservasjon(reservasjon.getReservertTil()));
        reservasjonRepository.lagre(reservasjon);
        return reservasjon;
    }

    public Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime reservertTil) {
        var reservasjon = oppgaveRepository.hentReservasjon(oppgaveId)
            .orElseThrow(() -> new IllegalStateException(FANT_IKKE_RESERVASJON_TILKNYTTET_OPPGAVE_ID + oppgaveId));
        reservasjon.setReservertTil(reservertTil);
        reservasjonRepository.lagre(reservasjon);
        return reservasjon;
    }

    public List<OppgaveBehandlingStatusWrapper> hentSaksbehandlersSisteReserverteMedStatus(boolean kunAktive) {
        var sisteReserverteMetadata = reservasjonRepository.hentSisteReserverteMetadata(BrukerIdent.brukerIdent(), kunAktive);
        var oppgaveIder = sisteReserverteMetadata.stream().map(SisteReserverteMetadata::oppgaveId).toList();
        var oppgaveListe = oppgaveRepository.hentOppgaverReadOnly(oppgaveIder);
        var oppgaveMap = oppgaveListe.stream().collect(Collectors.toMap(Oppgave::getId, Function.identity()));
        return sisteReserverteMetadata.stream().map(mr -> {
            var oppgave = oppgaveMap.get(mr.oppgaveId());
            var status = mapStatus(oppgave, mr.sisteEventType());
            return new OppgaveBehandlingStatusWrapper(oppgave, status);
        }).toList();

    }

    private static OppgaveBehandlingStatus mapStatus(Oppgave oppgave, OppgaveEventType sisteEventType) {
        var erTilBeslutter = oppgave.getOppgaveEgenskaper().stream()
            .anyMatch(egenskap -> AndreKriterierType.TIL_BESLUTTER.equals(egenskap.getAndreKriterierType()));
        var erReturnertFraBeslutter = oppgave.getOppgaveEgenskaper().stream()
            .anyMatch(egenskap -> AndreKriterierType.RETURNERT_FRA_BESLUTTER.equals(egenskap.getAndreKriterierType()));
        if (sisteEventType.erVenteEvent()) {
            return OppgaveBehandlingStatus.PÅ_VENT;
        } else if (!oppgave.getAktiv()) {
            return OppgaveBehandlingStatus.FERDIG;
        } else if (erTilBeslutter) {
            return OppgaveBehandlingStatus.TIL_BESLUTTER;
        } else if (erReturnertFraBeslutter) {
            return OppgaveBehandlingStatus.RETURNERT_FRA_BESLUTTER;
        } else {
            return OppgaveBehandlingStatus.UNDER_ARBEID;
        }
    }

    public void opprettReservasjonOgLagre(Oppgave oppgave, String saksbehandler, String begrunnelse) {
        var reservasjon = opprettReservasjon(oppgave, saksbehandler, begrunnelse);
        reservasjonRepository.lagre(reservasjon);
    }

    public static Reservasjon opprettReservasjon(Oppgave oppgave, String saksbehandler, String begrunnelse) {
        var reservertTil = utvidetReservasjon();
        var reservasjon = new Reservasjon(oppgave);
        reservasjon.setReservertAv(saksbehandler);
        reservasjon.setBegrunnelse(begrunnelse);
        reservasjon.setReservertTil(reservertTil);
        reservasjon.setFlyttetAv(BrukerIdent.brukerIdent());
        reservasjon.setFlyttetTidspunkt(LocalDateTime.now());
        return reservasjon;
    }


    public void reserverBasertPåAvsluttetReservasjon(Oppgave oppgave, Reservasjon gammelReservasjon) {
        var reservasjon = new Reservasjon(oppgave);
        reservasjon.setReservertTil(standardReservasjon());
        reservasjon.setReservertAv(gammelReservasjon.getReservertAv());
        reservasjon.setBegrunnelse(gammelReservasjon.getBegrunnelse());
        reservasjonRepository.lagre(reservasjon);
    }

    public Reservasjon reserverOppgaveBasertPåEksisterendeReservasjon(Oppgave oppgave, Reservasjon reservasjon, LocalDateTime nyVarighetTil) {
        var nyReservasjon = new Reservasjon(oppgave);
        nyReservasjon.setReservertTil(nyVarighetTil);
        nyReservasjon.setReservertAv(reservasjon.getReservertAv());
        nyReservasjon.setFlyttetTidspunkt(reservasjon.getFlyttetTidspunkt());
        nyReservasjon.setFlyttetAv(reservasjon.getFlyttetAv());
        nyReservasjon.setBegrunnelse(reservasjon.getBegrunnelse());
        reservasjonRepository.lagre(nyReservasjon);
        reservasjonRepository.refresh(oppgave);
        return nyReservasjon;
    }

    private static LocalDateTime utvidetReservasjon(LocalDateTime eksisterende) {
        return eksisterende.plusHours(24).with(DateAndTimeUtil.justerTilNesteUkedag);
    }

    private static LocalDateTime utvidetReservasjon() {
        return LocalDate.now().plusDays(1).with(DateAndTimeUtil.justerTilNesteUkedag).atTime(19, 0);
    }

    private static LocalDateTime standardReservasjon() {
        return LocalDateTime.now().plusHours(8).with(DateAndTimeUtil.justerTilNesteUkedag);
    }
}
