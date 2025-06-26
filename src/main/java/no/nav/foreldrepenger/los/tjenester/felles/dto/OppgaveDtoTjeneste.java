package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.los.reservasjon.OppgaveBehandlingStatusWrapper;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.persontjeneste.IkkeTilgangPåPersonException;
import no.nav.foreldrepenger.los.persontjeneste.PersonTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.server.abac.TilgangFilterKlient;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.sikkerhet.kontekst.KontekstHolder;
import no.nav.vedtak.sikkerhet.kontekst.RequestKontekst;

@ApplicationScoped
public class OppgaveDtoTjeneste {

    public static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;
    public static final int ANTALL_OPPGAVER_UTVALG = 19;


    private static final Logger LOG = LoggerFactory.getLogger(OppgaveDtoTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private PersonTjeneste personTjeneste;
    private ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private TilgangFilterKlient filterKlient;
    private ReservasjonRepository reservasjonRepository;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              ReservasjonTjeneste reservasjonTjeneste,
                              PersonTjeneste personTjeneste,
                              ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste,
                              OppgaveKøTjeneste oppgaveKøTjeneste,
                              TilgangFilterKlient filterKlient, ReservasjonRepository reservasjonRepository) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.personTjeneste = personTjeneste;
        this.reservasjonStatusDtoTjeneste = reservasjonStatusDtoTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.filterKlient = filterKlient;
        this.reservasjonRepository = reservasjonRepository;
    }

    OppgaveDtoTjeneste() {
        //CDI
    }

    /**
     * @param oppgave Metoden skal kun brukes ved kall fra endepunkt som tar inn {@link OppgaveIdDto}.
     *                Det er ordinær tilgangssjekk, men ikke videre oppslag i PDL
     * @return ReservasjonStatusDto
     */
    public ReservasjonStatusDto lagOppgaveStatusUtenPersonoppslag(Oppgave oppgave) {
        return reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
    }

    public boolean finnesTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        return !oppgaveKøTjeneste.hentOppgaver(sakslisteId.getVerdi()).isEmpty();
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_UTVALG);
        var oppgaveDtos = map(nesteOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, nesteOppgaver.size() == ANTALL_OPPGAVER_UTVALG);
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (oppgaveDtos.size() == Math.min(ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, nesteOppgaver.size())) {
            return oppgaveDtos;
        }
        LOG.info("{} behandlinger filtrert bort for saksliste {}", nesteOppgaver.size() - oppgaveDtos.size(), sakslisteId);
        var alleOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId);
        return map(alleOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, false);
    }

    public List<OppgaveDto> getSaksbehandlersReserverteAktiveOppgaver() {
        var oppgaver = reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver();
        return map(oppgaver);
    }

    public List<OppgaveDtoMedStatus> getSaksbehandlersSisteReserverteOppgaver() {
        var oppgaverMedStatus = reservasjonTjeneste.hentSaksbehandlersSisteReserverteMedStatus();
        var oppgaver = oppgaverMedStatus.stream().map(OppgaveBehandlingStatusWrapper::oppgave).toList();
        var saksnummerMedTilgang = filterHarTilgang(oppgaver);
        var oppgaverMedTilgang = oppgaver.stream()
            .filter(oppgave -> saksnummerMedTilgang.contains(oppgave.getSaksnummer()))
            .toList();
        return oppgaverMedTilgang.stream()
            .map(this::safeLagDtoFor)
            .flatMap(Optional::stream)
            .map(dto -> {
                var status = oppgaverMedStatus.stream()
                    .filter(oppgaveStatus -> oppgaveStatus.oppgave().getId().equals(dto.getId()))
                    .findFirst()
                    .map(OppgaveBehandlingStatusWrapper::status)
                    .orElseThrow();
                return new OppgaveDtoMedStatus(dto, status);
            })
            .toList();
    }

    private Optional<OppgaveDto> safeLagDtoFor(Oppgave oppgave) {
        try {
            return Optional.of(lagDtoFor(oppgave));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<OppgaveDto> hentOppgaverForFagsaker(Collection<Saksnummer> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return map(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver) {
        return map(oppgaver, oppgaver.size(), false);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall, boolean randomiser) {
        if (oppgaver.isEmpty()) {
            return List.of();
        } else if (oppgaver.size() <= maksAntall) {
            return lagDtoForFilterTilgang(oppgaver);
        } else {
            List<OppgaveDto> dtoList = new ArrayList<>();
            var antallOppgaver = oppgaver.size();
            var permutert = randomiser ? shuffleList(oppgaver, antallOppgaver) : oppgaver;
            var inkrement = Math.min(ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, maksAntall);
            for (int i = 0; i < antallOppgaver && dtoList.size() < maksAntall; i += inkrement) {
                var sjekkOppgaver = permutert.subList(i, Math.min(i + inkrement, antallOppgaver));
                dtoList.addAll(lagDtoForFilterTilgang(sjekkOppgaver));
            }
            return dtoList.stream().limit(maksAntall).toList();
        }
    }

    private List<Oppgave> shuffleList(List<Oppgave> oppgaver, int antallOppgaver) {
        var start = KontekstHolder.getKontekst() instanceof RequestKontekst rk
            ? Math.abs(rk.getUid().hashCode() + LocalDate.now().getDayOfMonth()) % antallOppgaver
            : (int)(System.currentTimeMillis() % antallOppgaver);
        List<Oppgave> permutert = new ArrayList<>();
        for (int i = 0; i < antallOppgaver; i++) {
            permutert.add(oppgaver.get((start + i) % antallOppgaver));
        }
        return permutert;
    }

    private List<OppgaveDto> lagDtoForFilterTilgang(List<Oppgave> oppgaver) {
        var saksnummerMedTilgang = filterHarTilgang(oppgaver);
        return oppgaver.stream()
            .filter(oppgave -> saksnummerMedTilgang.contains(oppgave.getSaksnummer()))
            .map(this::lagEnkelDto)
            .flatMap(Collection::stream)
            .toList();
    }

    private List<OppgaveDto> lagEnkelDto(Oppgave oppgave) {
        try {
            return List.of(lagDtoFor(oppgave));
        } catch (IkkeTilgangPåPersonException e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, oppslag PDL feiler på grunn av manglende tilgang", oppgave.getId(), e);
        } catch (LagOppgaveDtoFeil e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, hopper over", oppgave.getId(), e);
        } catch (Exception e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, annen feil", oppgave.getId(), e);
        }
        return List.of();
    }

    private OppgaveDto lagDtoFor(Oppgave oppgave) {
        var person = personTjeneste.hentPerson(oppgave.getFagsakYtelseType(), oppgave.getAktørId(), oppgave.getSaksnummer())
            .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
        var oppgaveStatus = reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, person, oppgaveStatus);
    }

    private Set<Saksnummer> filterHarTilgang(List<Oppgave> oppgaver) {
        var kontekst = KontekstHolder.getKontekst() instanceof RequestKontekst rk ? rk : null;
        if (kontekst == null || oppgaver.isEmpty()) {
            return Set.of();
        }
        return filterKlient.tilgangFilterSaker(kontekst.getOid(), oppgaver);
    }

    public static final class LagOppgaveDtoFeil extends RuntimeException {
        public LagOppgaveDtoFeil(String message) {
            super(message);
        }
    }
}
