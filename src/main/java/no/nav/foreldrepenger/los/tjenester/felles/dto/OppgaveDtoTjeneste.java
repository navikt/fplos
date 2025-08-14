package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.los.reservasjon.OppgaveBehandlingStatusWrapper;

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
     * @param oppgave Metoden skal kun brukes ved kall fra endepunkt som tar inn {@link OppgaveIdDto}.
     *                Det er ordinær tilgangssjekk, men ikke videre oppslag i PDL
     * @return ReservasjonStatusDto
     */
    public ReservasjonStatusDto lagOppgaveStatusUtenPersonoppslag(Oppgave oppgave) {
        return reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_UTVALG);
        var oppgaveDtos = map(nesteOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, nesteOppgaver.size() == ANTALL_OPPGAVER_UTVALG);
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (oppgaveDtos.size() == Math.min(ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, nesteOppgaver.size())) {
            return oppgaveDtos;
        }
        LOG.info("{} behandlinger filtrert bort for saksliste {}", nesteOppgaver.size() - oppgaveDtos.size(), sakslisteId);
        var alleOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, 150);
        return map(alleOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, false);
    }

    public List<OppgaveDto> getSaksbehandlersReserverteAktiveOppgaver() {
        var oppgaver = reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver();
        return lagDtoMedTilgangskontroll(oppgaver);
    }

    public List<OppgaveDtoMedStatus> getSaksbehandlersSisteReserverteOppgaver() {
        var oppgaverMedStatus = reservasjonTjeneste.hentSaksbehandlersSisteReserverteMedStatus();
        var oppgaver = oppgaverMedStatus.stream().map(OppgaveBehandlingStatusWrapper::oppgave).toList();
        return lagDtoMedTilgangskontroll(oppgaver).stream()
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

    public List<OppgaveDto> hentOppgaverForFagsaker(Collection<Saksnummer> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return lagDtoMedTilgangskontroll(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall, boolean randomiser) {
        if (oppgaver.isEmpty()) {
            return List.of();
        } else if (oppgaver.size() <= maksAntall) {
            return lagDtoMedTilgangskontroll(oppgaver);
        } else {
            List<OppgaveDto> dtoList = new ArrayList<>();
            var antallOppgaver = oppgaver.size();
            var permutert = randomiser ? shuffleList(oppgaver, antallOppgaver) : oppgaver;
            var inkrement = Math.min(ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, maksAntall);
            for (int i = 0; i < antallOppgaver && dtoList.size() < maksAntall; i += inkrement) {
                var sjekkOppgaver = permutert.subList(i, Math.min(i + inkrement, antallOppgaver));
                dtoList.addAll(lagDtoMedTilgangskontroll(sjekkOppgaver));
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

    private List<OppgaveDto> lagDtoMedTilgangskontroll(List<Oppgave> oppgaver) {
        var saksnummerMedTilgang = filterKlient.tilgangFilterSaker(oppgaver);
        return oppgaver.stream()
            .filter(oppgave -> saksnummerMedTilgang.contains(oppgave.getSaksnummer()))
            .map(this::lagDto)
            .flatMap(Optional::stream)
            .toList();
    }

    private Optional<OppgaveDto> lagDto(Oppgave oppgave) {
        try {
            var person = personTjeneste.hentPerson(oppgave.getFagsakYtelseType(), oppgave.getAktørId(), oppgave.getSaksnummer())
                .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
            var oppgaveStatus = reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
            return Optional.of(new OppgaveDto(oppgave, person, oppgaveStatus));
        } catch (IkkeTilgangPåPersonException e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, oppslag PDL feiler på grunn av manglende tilgang", oppgave.getId(), e);
        } catch (LagOppgaveDtoFeil e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, hopper over", oppgave.getId(), e);
        } catch (Exception e) {
            LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, annen feil", oppgave.getId(), e);
        }
        return Optional.empty();
    }

    public static final class LagOppgaveDtoFeil extends RuntimeException {
        public LagOppgaveDtoFeil(String message) {
            super(message);
        }
    }
}
