package no.nav.fplos.oppgave;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.Oppgavespørring;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

@ApplicationScoped
public class OppgaveTjenesteImpl implements OppgaveTjeneste {

    private static final Logger log = LoggerFactory.getLogger(OppgaveTjenesteImpl.class);
    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;

    OppgaveTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public OppgaveTjenesteImpl(OppgaveRepository oppgaveRepository,
                               OrganisasjonRepository organisasjonRepository) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
    }

    @Override
    public List<Oppgave> hentOppgaver(Long sakslisteId) {
        return hentOppgaver(sakslisteId, 0);
    }

    @Override
    public List<Oppgave> hentOppgaver(Long sakslisteId, int maksAntall) {
        log.debug("Henter oppgaver for saksliste : " + sakslisteId);
        try {
            OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentFiltrering(sakslisteId);
            if (oppgaveFiltrering == null) {
                return Collections.emptyList();
            }
            List<Oppgave> oppgaver = oppgaveRepository.hentOppgaver(new Oppgavespørring(oppgaveFiltrering), maksAntall);
            log.debug("Antall oppgaver hentet: " + oppgaver.size());
            return oppgaver;
        } catch (Exception e) {
            log.error("Henting av oppgave feilet, returnerer en tom oppgaveliste", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(fagsakSaksnummerListe);
    }


    public List<Reservasjon> hentReservasjonerTilknyttetAktiveOppgaver() {
        return oppgaveRepository.hentReservasjonerTilknyttetAktiveOppgaver(finnBrukernavn());
    }

    public List<Reservasjon> hentReservasjonerForAvdeling(String avdelingEnhet) {
        return oppgaveRepository.hentAlleReservasjonerForAvdeling(avdelingEnhet);
    }

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
                log.info("Antatt kollisjon på reservasjon", e);
                oppgaveRepository.refresh(reservasjon.getOppgave());
            }
        }
        return reservasjon;
    }

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

    public Reservasjon forlengReservasjonPåOppgave(Long oppgaveId) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.forlengReservasjonPåOppgave();
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
        return reservasjon;
    }

    public Reservasjon endreReservasjonPåOppgave(Long oppgaveId, LocalDateTime reservertTil) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.endreReservasjonPåOppgave(reservertTil);
        oppgaveRepository.lagre(reservasjon);
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
    public List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent) {
        return organisasjonRepository.hentSaksbehandlerHvisEksisterer(brukerIdent)
                .map(Saksbehandler::getOppgaveFiltreringer)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker() {
        return hentAlleOppgaveFiltrering(finnBrukernavn());
    }

    @Override
    public Integer hentAntallOppgaver(Long behandlingsKø, boolean forAvdelingsleder) {
        int antallOppgaver = 0;
        try {
            OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentFiltrering(behandlingsKø);
            if (oppgaveFiltrering != null) {
                var queryDto = new Oppgavespørring(oppgaveFiltrering);
                queryDto.setForAvdelingsleder(forAvdelingsleder);
                antallOppgaver = oppgaveRepository.hentAntallOppgaver(queryDto);
            }
        } catch (Exception e) {
            log.error("Henting av oppgave feilet", e);
        }
        return antallOppgaver;
    }

    @Override
    public Integer hentAntallOppgaverForAvdeling(String avdelingsEnhet) {
        Avdeling avdeling = organisasjonRepository.hentAvdelingFraEnhet(avdelingsEnhet);
        return oppgaveRepository.hentAntallOppgaverForAvdeling(avdeling.getId());
    }

    public boolean harForandretOppgaver(List<Long> oppgaveIder) {
        List<Oppgave> oppgaver = oppgaveRepository.sjekkOmOppgaverFortsattErTilgjengelige(oppgaveIder);
        return oppgaver.size() != oppgaveIder.size();
    }

    @Override
    public List<Oppgave> hentSisteReserverteOppgaver() {
        return oppgaveRepository.hentSisteReserverteOppgaver(finnBrukernavn());
    }

    private static String finnBrukernavn() {
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

    @Override
    public Oppgave hentOppgave(Long oppgaveId) {
        return oppgaveRepository.hentOppgave(oppgaveId);
    }

    @Override
    public Oppgave hentSisteOppgave(BehandlingId behandlingId) {
        var oppgaver = oppgaveRepository.hentOppgaver(behandlingId);
        var aktivOppgave = oppgaver.stream().filter(oppgave -> oppgave.getAktiv()).findFirst();
        return aktivOppgave.orElseGet(() -> sisteAvsluttet(oppgaver));
    }

    private Oppgave sisteAvsluttet(List<Oppgave> oppgaver) {
        return oppgaver.stream()
                .sorted((o1, o2) -> o2.getOppgaveAvsluttet().compareTo(o1.getOppgaveAvsluttet()))
                .findFirst()
                .orElseThrow();
    }

}
