package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;

@ApplicationScoped
public class OppgaveDtoTjeneste {

    public static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(OppgaveRestTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private TpsTjeneste tpsTjeneste;
    private OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              TpsTjeneste tpsTjeneste,
                              OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.tpsTjeneste = tpsTjeneste;
        this.oppgaveStatusDtoTjeneste = oppgaveStatusDtoTjeneste;
    }

    OppgaveDtoTjeneste() {
        //CDI
    }

    public OppgaveDto lagDtoFor(Oppgave oppgave) throws IkkeTilgangPåOppgaveException {
        TpsPersonDto tpsPersonDto;
        try {
            tpsPersonDto = tpsTjeneste.hentBrukerForAktør(oppgave.getAktorId());
        } catch (HentPersonSikkerhetsbegrensning e) {
            throw new IkkeTilgangPåOppgaveException(oppgave.getId(), e);
        }
        var oppgaveStatus = oppgaveStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, tpsPersonDto, oppgaveStatus);
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
        var oppgaveDtos = map(nesteOppgaver);
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (nesteOppgaver.size() == oppgaveDtos.size()) {
            return oppgaveDtos;
        }
        LOGGER.info("{} behandlinger filtrert bort for saksliste {}", nesteOppgaver.size() - oppgaveDtos.size(), sakslisteId);
        var alleOppgaver = oppgaveTjeneste.hentOppgaver(sakslisteId);
        return map(alleOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
    }

    public List<OppgaveDto> getReserverteOppgaver() {
        var reserveringer = oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver();
        var oppgaver = reserveringer.stream()
                .map(r -> r.getOppgave())
                .collect(Collectors.toList());
        return map(oppgaver);
    }

    public List<OppgaveDto> getBehandledeOppgaver() {
        var sistReserverteOppgaver = oppgaveTjeneste.hentSisteReserverteOppgaver();
        return map(sistReserverteOppgaver);
    }

    public List<OppgaveDto> hentOppgaverForFagsaker(List<Long> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return map(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver) {
        return map(oppgaver, oppgaver.size());
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall) {
        List<OppgaveDto> dtoList = new ArrayList<>();
        for (int i = 0; i < oppgaver.size() && dtoList.size() < maksAntall; i++) {
            var oppgave = oppgaver.get(i);
            try {
                dtoList.add(lagDtoFor(oppgave));
            } catch (IkkeTilgangPåOppgaveException e) {
                logBegrensning(oppgave, e);
            }
        }
        return dtoList;
    }

    private void logBegrensning(Oppgave oppgave, IkkeTilgangPåOppgaveException e) {
        LOGGER.info("Prøver å slå opp i tps uten å ha tilgang. Ignorerer oppgave {}", oppgave.getId(), e);
    }
}
