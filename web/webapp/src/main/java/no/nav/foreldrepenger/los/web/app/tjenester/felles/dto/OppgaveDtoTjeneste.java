package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.klient.person.IkkeTilgangPåPersonException;
import no.nav.foreldrepenger.los.klient.person.PersonTjeneste;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.AbacAttributter;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

@ApplicationScoped
public class OppgaveDtoTjeneste {

    public static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveDtoTjeneste.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private PersonTjeneste personTjeneste;
    private OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste;
    private PdpKlient pdpKlient;
    private PdpRequestBuilder pdpRequestBuilder;
    private OppgaveKøTjeneste oppgaveKøTjeneste;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              ReservasjonTjeneste reservasjonTjeneste,
                              PersonTjeneste personTjeneste,
                              OppgaveStatusDtoTjeneste oppgaveStatusDtoTjeneste,
                              PdpKlient pdpKlient,
                              PdpRequestBuilder pdpRequestBuilder,
                              OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.personTjeneste = personTjeneste;
        this.oppgaveStatusDtoTjeneste = oppgaveStatusDtoTjeneste;
        this.pdpKlient = pdpKlient;
        this.pdpRequestBuilder = pdpRequestBuilder;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
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
            sjekkTilgang(oppgave);
        }
        var person = personTjeneste.hentPerson(oppgave.getAktorId(), String.valueOf(oppgave.getFagsakSaksnummer()))
                .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
        var oppgaveStatus = oppgaveStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, person, oppgaveStatus);
    }

    public boolean finnesTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        return oppgaveKøTjeneste.hentOppgaver(sakslisteId.getVerdi())
                .stream()
                .anyMatch(this::harTilgang);
    }

    private boolean harTilgang(Oppgave oppgave) {
        var abacAttributtSamling = abacAttributtSamling(oppgave);
        var pdpRequest = pdpRequestBuilder.lagPdpRequest(abacAttributtSamling);
        var tilgangsbeslutning = pdpKlient.forespørTilgang(pdpRequest);
        return tilgangsbeslutning.fikkTilgang();
    }

    private void sjekkTilgang(Oppgave oppgave) {
        if (!harTilgang(oppgave)) {
            throw new IkkeTilgangPåBehandlingException(oppgave.getBehandlingId());
        }
    }

    private AbacAttributtSamling abacAttributtSamling(Oppgave oppgave) {
        return AbacAttributtSamling
                .medJwtToken(SubjectHandler.getSubjectHandler().getInternSsoToken())
                .setActionType(BeskyttetRessursActionAttributt.READ)
                .setResource(AbacAttributter.FAGSAK)
                .leggTil(AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVE_ID, oppgave.getId()));
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
        var oppgaveDtos = map(nesteOppgaver);
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (nesteOppgaver.size() == oppgaveDtos.size()) {
            return oppgaveDtos;
        }
        LOG.info("{} behandlinger filtrert bort for saksliste {}", nesteOppgaver.size() - oppgaveDtos.size(), sakslisteId);
        var alleOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId);
        return map(alleOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER);
    }

    public List<OppgaveDto> getSaksbehandlersReserverteAktiveOppgaver() {
        var oppgaver = reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver();
        return map(oppgaver);
    }

    public List<OppgaveDto> getSaksbehandlersSisteReserverteOppgaver() {
        return reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()
                .stream()
                .map(this::tilDtoSvelgFeil)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public List<OppgaveDto> hentOppgaverForFagsaker(List<Long> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return map(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver) {
        return map(oppgaver, oppgaver.size());
    }

    private Optional<OppgaveDto> tilDtoSvelgFeil(Oppgave oppgave) {
        try {
            return Optional.of(lagDtoFor(oppgave, true));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall) {
        List<OppgaveDto> dtoList = new ArrayList<>();
        for (var i = 0; i < oppgaver.size() && dtoList.size() < maksAntall; i++) {
            var oppgave = oppgaver.get(i);
            try {
                dtoList.add(lagDtoFor(oppgave, true));
            } catch (IkkeTilgangPåBehandlingException e) {
                logBegrensning(oppgave);
            } catch (IkkeTilgangPåPersonException e) {
                LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, oppslag PDL feiler på grunn av manglende tilgang", oppgave.getId(), e);
            } catch (LagOppgaveDtoFeil e) {
                LOG.warn("Kunne ikke lage OppgaveDto for oppgaveId {}, hopper over", oppgave.getId(), e);
            }
        }
        return dtoList;
    }

    private void logBegrensning(Oppgave oppgave) {
        LOG.info("Prøver å slå opp oppgave uten å ha tilgang. Ignorerer oppgave {}", oppgave.getId());
    }

    public static final class LagOppgaveDtoFeil extends RuntimeException {
        public LagOppgaveDtoFeil(String message) {
            super(message);
        }
    }
}
