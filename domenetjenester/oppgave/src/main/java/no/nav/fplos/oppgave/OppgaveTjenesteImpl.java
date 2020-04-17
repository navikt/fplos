package no.nav.fplos.oppgave;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.Oppgavespørring;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
public class OppgaveTjenesteImpl implements OppgaveTjeneste {

    private static final Logger log = LoggerFactory.getLogger(OppgaveTjenesteImpl.class);
    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private AnsattTjeneste ansattTjeneste;

    OppgaveTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public OppgaveTjenesteImpl(OppgaveRepository oppgaveRepository,
                               OrganisasjonRepository organisasjonRepository,
                               AvdelingslederTjeneste avdelingslederTjeneste,
                               AnsattTjeneste ansattTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.organisasjonRepository = organisasjonRepository;
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.ansattTjeneste = ansattTjeneste;
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
        if (reservasjon.getReservertTil() == null || !reservasjon.erAktiv()) {
            reservasjon.reserverNormalt();
        }
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(reservasjon.getOppgave());
        oppgaveRepository.lagre(new ReservasjonEventLogg(reservasjon));
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
        return organisasjonRepository.hentMuligSaksbehandler(brukerIdent)
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
    public List<SaksbehandlerinformasjonDto> hentSakslistensSaksbehandlere(Long sakslisteId) {
        OppgaveFiltrering oppgaveFiltrering = avdelingslederTjeneste.hentOppgaveFiltering(sakslisteId);
        return oppgaveFiltrering.getSaksbehandlere()
                .stream()
                .map(s -> lagSaksbehandlerinformasjonDto(s.getSaksbehandlerIdent()))
                .collect(Collectors.toList());
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
    public SaksbehandlerinformasjonDto hentSaksbehandlerNavnOgAvdelinger(String ident) {
        List<Saksbehandler> saksbehandlere = organisasjonRepository.hentAlleSaksbehandlere();
        if (saksbehandlere.stream().noneMatch(saksbehandler -> saksbehandler.getSaksbehandlerIdent().equals(ident))) {
            return null;
        }

        if (hentAlleOppgaveFiltrering(ident).isEmpty()) {
            return null;
        }

        return lagSaksbehandlerinformasjonDto(ident);
    }

    @Override
    public String hentNavnHvisReservertAvAnnenSaksbehandler(Reservasjon reservasjon) {
        String innloggetBruker = SubjectHandler.getSubjectHandler().getUid();
        boolean reservertAvAnnenSaksbehandler = reservasjon != null &&
                reservasjon.getReservertAv() != null &&
                !reservasjon.getReservertAv().equalsIgnoreCase(innloggetBruker) &&
                reservasjon.getReservertTil() != null &&
                reservasjon.erAktiv();
        return reservertAvAnnenSaksbehandler ? ansattTjeneste.hentAnsattNavn(reservasjon.getReservertAv()) : null;
    }

    @Override
    public String hentNavnHvisFlyttetAvSaksbehandler(String flyttetAv) {
        boolean flyttetAvSaksbehandler = flyttetAv != null;
        return flyttetAvSaksbehandler ? ansattTjeneste.hentAnsattNavn(flyttetAv) : null;
    }

    private SaksbehandlerinformasjonDto lagSaksbehandlerinformasjonDto(String ident) {
        return new SaksbehandlerinformasjonDto(ident, hentSaksbehandlerNavn(ident), ansattTjeneste.hentAvdelingerNavnForAnsatt(ident));
    }

    private String hentSaksbehandlerNavn(String ident) {
        try {
            return ansattTjeneste.hentAnsattNavn(ident);
        } catch (IntegrasjonException e) {
            log.info("Henting av ansattnavn feilet, fortsetter med ukjent navn.", e);
            return "Ukjent ansatt";
        }
    }

}
