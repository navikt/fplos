package no.nav.fplos.oppgave;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.oppgave.ReservasjonEventLogg;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgavespørringDto;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OppgaveTjenesteImpl implements OppgaveTjeneste {

    private static final Logger log = LoggerFactory.getLogger(OppgaveTjenesteImpl.class);
    private OppgaveRepository oppgaveRepository;
    private OrganisasjonRepository organisasjonRepository;
    private TpsTjeneste tpsTjeneste;
    private AvdelingslederTjeneste avdelingslederTjeneste;
    private AnsattTjeneste ansattTjeneste;

    OppgaveTjenesteImpl() {
        // for CDI proxy
    }

    @Inject
    public OppgaveTjenesteImpl(OppgaveRepositoryProvider oppgaveRepositoryProvider,
                               TpsTjeneste tpsTjeneste,
                               AvdelingslederTjeneste avdelingslederTjeneste,
                               AnsattTjeneste ansattTjeneste) {
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        this.organisasjonRepository = oppgaveRepositoryProvider.getOrganisasjonRepository();
        this.tpsTjeneste = tpsTjeneste;
        this.avdelingslederTjeneste = avdelingslederTjeneste;
        this.ansattTjeneste = ansattTjeneste;
    }

    @Override
    public List<Oppgave> hentOppgaver(Long sakslisteId){
        List<Oppgave> oppgaver = new ArrayList<>();
        try {
            OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(sakslisteId);
            oppgaver = oppgaveRepository.hentOppgaver(new OppgavespørringDto(oppgaveFiltrering));
        }catch (Exception e){
            log.error("Henting av oppgave feilet",e);
        }
        return oppgaver;
    }

    @Override
    public List<Oppgave> hentNesteOppgaver(Long sakslisteId){
        return hentOppgaver(sakslisteId);
    }

    @Override
    public List<Oppgave> hentOppgaverForSaksnummer(Long fagsakSaksnummer) {
        return oppgaveRepository.hentOppgaverForSaksnummer(fagsakSaksnummer);
    }

    @Override
    public List<Oppgave> hentAktiveOppgaverForSaksnummer(Collection<Long> fagsakSaksnummerListe) {
        return oppgaveRepository.hentAktiveOppgaverForSaksnummer(fagsakSaksnummerListe);
    }


    public List<Reservasjon> hentReserverteOppgaver(){
        return oppgaveRepository.hentReserverteOppgaver(finnBrukernavn());
    }


    public Reservasjon reserverOppgave(Long oppgaveId){
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        if(reservasjon.getReservertTil() == null || reservasjon.getReservertTil().isBefore(LocalDateTime.now())) {
            reservasjon.reserverNormalt();
        }
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(reservasjon.getOppgave());
        oppgaveRepository.lagre(new ReservasjonEventLogg(oppgaveId, reservasjon));
        return reservasjon;
    }

    public Reservasjon hentReservasjon(Long oppgaveId){
        return oppgaveRepository.hentReservasjon(oppgaveId);
    }

    @Override
    public Reservasjon frigiOppgave(Long oppgaveId, String begrunnelse){
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        Oppgave oppgave = reservasjon.getOppgave();
        reservasjon.frigiOppgave(reservasjon.getReservertAv(), begrunnelse);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(oppgave);
        oppgaveRepository.lagre(new ReservasjonEventLogg(oppgaveId, reservasjon));
        return reservasjon;
    }

    public Reservasjon forlengReservasjonPåOppgave(Long oppgaveId){
        Reservasjon oppgave = oppgaveRepository.hentReservasjon(oppgaveId);
        oppgave.forlengReservasjonPåOppgave();
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.lagre(new ReservasjonEventLogg(oppgaveId, oppgave));
        return oppgave;
    }

    @Override
    public Reservasjon flyttReservasjon(Long oppgaveId, String brukernavn, String begrunnelse) {
        Reservasjon reservasjon = oppgaveRepository.hentReservasjon(oppgaveId);
        reservasjon.flyttReservasjon(brukernavn, begrunnelse);
        oppgaveRepository.lagre(reservasjon);
        oppgaveRepository.refresh(reservasjon.getOppgave());
        oppgaveRepository.lagre(new ReservasjonEventLogg(oppgaveId, reservasjon));
        return reservasjon;
    }

    @Override
    public List<OppgaveFiltrering> hentAlleOppgaveFiltrering(String brukerIdent) {
        Optional<Saksbehandler> saksbehandler = organisasjonRepository.hentMuligSaksbehandler(brukerIdent);
        return saksbehandler.isPresent() ? saksbehandler.get().getOppgaveFiltreringer() : Collections.emptyList();
    }

    @Override
    public List<OppgaveFiltrering> hentOppgaveFiltreringerForPåloggetBruker() {
        return hentAlleOppgaveFiltrering(finnBrukernavn());
    }

    @Override
    public Optional<TpsPersonDto> hentPersonInfoOptional(long aktørId) {
        AktørId aktørIdFromLong = new AktørId(aktørId);
        return tpsTjeneste.hentBrukerForAktør(aktørIdFromLong);
    }

    @Override
    public TpsPersonDto hentPersonInfo(long aktørId) {
        AktørId aktørIdFromLong = new AktørId(aktørId);
        return tpsTjeneste.hentBrukerForAktør(aktørIdFromLong)
                .orElseThrow(() -> OppgaveFeilmeldinger.FACTORY.identIkkeFunnet(aktørIdFromLong).toException());
    }

    @Override
    public Integer hentAntallOppgaver(Long behandlingsKø) {
        int antallOppgaver = 0;
        try {
            OppgaveFiltrering oppgaveFiltrering = oppgaveRepository.hentListe(behandlingsKø);
            antallOppgaver = oppgaveRepository.hentAntallOppgaver(new OppgavespørringDto(oppgaveFiltrering));
        } catch (Exception e) {
            log.error("Henting av oppgave feilet", e);
        }
        return antallOppgaver;
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
                .map(saksbehandler -> lagSaksbehandlerinformasjonDto(saksbehandler.getSaksbehandlerIdent()))
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
        if(saksbehandlere.stream().noneMatch(saksbehandler -> saksbehandler.getSaksbehandlerIdent().equals(ident))) {
            return null;
        }

        if(hentAlleOppgaveFiltrering(ident).isEmpty()) {
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
                reservasjon.getReservertTil().isAfter(LocalDateTime.now());
        return reservertAvAnnenSaksbehandler ? ansattTjeneste.hentAnsattNavn(reservasjon.getReservertAv()) : null;
    }

    @Override
    public String hentNavnHvisFlyttetAvSaksbehandler(String flyttetAv) {
        boolean flyttetAvSaksbehandler = flyttetAv != null;
        return flyttetAvSaksbehandler ? ansattTjeneste.hentAnsattNavn(flyttetAv) : null;
    }

    private SaksbehandlerinformasjonDto lagSaksbehandlerinformasjonDto(String ident) {
        return new SaksbehandlerinformasjonDto(ident, ansattTjeneste.hentAnsattNavn(ident), ansattTjeneste.hentAvdelingerNavnForAnsatt(ident));
    }

}
