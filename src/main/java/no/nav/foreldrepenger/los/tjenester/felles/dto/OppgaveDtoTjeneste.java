package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveDtoTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private PersonTjeneste personTjeneste;
    private ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private TilgangFilterKlient filterKlient;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              ReservasjonTjeneste reservasjonTjeneste,
                              PersonTjeneste personTjeneste,
                              ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste,
                              OppgaveKøTjeneste oppgaveKøTjeneste,
                              TilgangFilterKlient filterKlient) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.personTjeneste = personTjeneste;
        this.reservasjonStatusDtoTjeneste = reservasjonStatusDtoTjeneste;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
        this.filterKlient = filterKlient;
    }

    OppgaveDtoTjeneste() {
        //CDI
    }

    /**
     *
     */
    public OppgaveDto lagDtoFor(Oppgave oppgave) {
        var person = personTjeneste.hentPerson(oppgave.getFagsakYtelseType(), oppgave.getAktørId(), oppgave.getSaksnummer())
            .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
        var oppgaveStatus = reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, person, oppgaveStatus);
    }

    /**
     * @param oppgave Metoden skal kun brukes ved kall fra endepunkt som tar inn {@link OppgaveIdDto}.
     *                Tilgangssjekk dekkes for slike endepunkt gjennom {@link no.nav.vedtak.sikkerhet.abac.BeskyttetRessursInterceptor}.
     * @return
     */
    public ReservasjonStatusDto lagOppgaveStatusUtenTilgangsjekk(Oppgave oppgave) {
        return reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
    }

    public boolean finnesTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        var oppgaverForSaksliste = oppgaveKøTjeneste.hentOppgaver(sakslisteId.getVerdi());
        if (oppgaverForSaksliste.isEmpty()) {
            return false;
        }
        return !filterHarTilgang(oppgaverForSaksliste).isEmpty();
    }

    private Set<String> filterHarTilgang(List<Oppgave> oppgaver) {
        var kontekst = KontekstHolder.getKontekst() instanceof RequestKontekst rk ? rk : null;
        if (kontekst == null || oppgaver.isEmpty()) {
            return Set.of();
        }
        var saksnummer = oppgaver.stream().map(Oppgave::getSaksnummer).collect(Collectors.toSet());
        return filterKlient.tilgangFilterSaker(kontekst.getOid(), saksnummer);
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER * 7);
        var oppgaveDtos = map(nesteOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, nesteOppgaver.size() == ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER * 7);
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

    public List<OppgaveDto> getSaksbehandlersSisteReserverteOppgaver() {
        var oppgaver = reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver();
        var saksnummerMedTilgang = filterHarTilgang(oppgaver);
        var oppgaverMedTilgang = oppgaver.stream()
            .filter(oppgave -> saksnummerMedTilgang.contains(oppgave.getSaksnummer()))
            .toList();
        return oppgaverMedTilgang.stream()
            .map(this::safeLagDtoFor)
            .flatMap(Optional::stream)
            .limit(10)
            .toList();
    }

    private Optional<OppgaveDto> safeLagDtoFor(Oppgave oppgave) {
        try {
            return Optional.of(lagDtoFor(oppgave));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<OppgaveDto> hentOppgaverForFagsaker(List<Long> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return map(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver) {
        return map(oppgaver, oppgaver.size(), false);
    }


    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall, boolean randomiser) {
        if (oppgaver.isEmpty()) {
            return List.of();
        }
        var saksnummerMedTilgang = filterHarTilgang(oppgaver);
        var oppgaverMedTilgang = oppgaver.stream()
            .filter(oppgave -> saksnummerMedTilgang.contains(oppgave.getSaksnummer()))
            .toList();
        List<OppgaveDto> dtoList = new ArrayList<>();
        var antallOppgaver = oppgaverMedTilgang.size();
        int start = randomiser ? (int)(System.currentTimeMillis() % antallOppgaver) : 0;
        for (var i = 0; i < oppgaverMedTilgang.size() && dtoList.size() < maksAntall; i++) {
            var oppgave = oppgaverMedTilgang.get((start + i) % antallOppgaver);
            try {
                dtoList.add(lagDtoFor(oppgave));
            } catch (IkkeTilgangPåPersonException e) {
                LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, oppslag PDL feiler på grunn av manglende tilgang", oppgave.getId(), e);
            } catch (LagOppgaveDtoFeil e) {
                LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, hopper over", oppgave.getId(), e);
            }
        }
        return dtoList;
    }

    public static final class LagOppgaveDtoFeil extends RuntimeException {
        public LagOppgaveDtoFeil(String message) {
            super(message);
        }
    }
}
