package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
public class OppgaveDtoTjeneste {

    public static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(OppgaveRestTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private TpsTjeneste tpsTjeneste;
    private OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste;
    private PdpKlient pdpKlient;
    private PdpRequestBuilder pdpRequestBuilder;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              TpsTjeneste tpsTjeneste,
                              OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste,
                              PdpKlient pdpKlient,
                              PdpRequestBuilder pdpRequestBuilder) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.tpsTjeneste = tpsTjeneste;
        this.oppgaveStatusDtoTjeneste = oppgaveStatusDtoTjeneste;
        this.pdpKlient = pdpKlient;
        this.pdpRequestBuilder = pdpRequestBuilder;
    }

    OppgaveDtoTjeneste() {
        //CDI
    }

    /**
     * @param sjekkTilgangPåBehandling Ved alle kall som tar i mot {@link no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto}
     *                                 eller {@link no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto}
     *                                 sjekkes tilgangen til behandlingen/oppgaven ved mottaket av restkallet gjennom {@link no.nav.vedtak.sikkerhet.abac.BeskyttetRessursInterceptor}.
     *                                 Applikasjonen har noen kall som ikke går rett på en behandling/oppgave, men som returnerer oppgaver,
     *                                 i disse tilfellene må sjekkTilgangPåBehandling være true.
     *
     */
    public OppgaveDto lagDtoFor(Oppgave oppgave, boolean sjekkTilgangPåBehandling) throws IkkeTilgangPåBehandlingException {
        if (sjekkTilgangPåBehandling) {
            sjekkTilgang(oppgave.getBehandlingId());
        }
        var tpsPersonDto = tpsTjeneste.hentBrukerForAktør(oppgave.getAktorId());
        var oppgaveStatus = oppgaveStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, tpsPersonDto, oppgaveStatus);
    }

    public boolean harTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        return oppgaveTjeneste.hentOppgaver(sakslisteId.getVerdi())
                .stream()
                .anyMatch(o -> harTilgang(o.getBehandlingId()));
    }

    private boolean harTilgang(BehandlingId behandlingId) {
        var abacAttributtSamling = abacAttributtSamling(behandlingId);
        var pdpRequest = pdpRequestBuilder.lagPdpRequest(abacAttributtSamling);
        var tilgangsbeslutning = pdpKlient.forespørTilgang(pdpRequest);
        return tilgangsbeslutning.fikkTilgang();
    }

    private void sjekkTilgang(BehandlingId behandlingId) {
        if (!harTilgang(behandlingId)) {
            throw new IkkeTilgangPåBehandlingException(behandlingId);
        }
    }

    private AbacAttributtSamling abacAttributtSamling(BehandlingId behandlingId) {
        return AbacAttributtSamling
                .medJwtToken(SubjectHandler.getSubjectHandler().getInternSsoToken())
                .setActionType(BeskyttetRessursActionAttributt.READ)
                .setResource(BeskyttetRessursResourceAttributt.FAGSAK.getEksternKode())
                .leggTil(AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, behandlingId.toUUID()));
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
                dtoList.add(lagDtoFor(oppgave, true));
            } catch (IkkeTilgangPåBehandlingException e) {
                logBegrensning(oppgave);
            }
        }
        return dtoList;
    }

    private void logBegrensning(Oppgave oppgave) {
        LOGGER.info("Prøver å slå opp oppgave uten å ha tilgang. Ignorerer oppgave {}", oppgave.getId());
    }
}