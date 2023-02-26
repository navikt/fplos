package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.felles.util.OptionalUtil.tryOrEmpty;
import static no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste.OPPGAVER_BASE_PATH;
import static no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste.OPPGAVER_STATUS_PATH;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.klient.person.IkkeTilgangPåPersonException;
import no.nav.foreldrepenger.los.klient.person.PersonTjeneste;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.Token;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;
import no.nav.vedtak.sikkerhet.abac.internal.BeskyttetRessursAttributter;
import no.nav.vedtak.sikkerhet.kontekst.KontekstHolder;
import no.nav.vedtak.sikkerhet.kontekst.RequestKontekst;

@ApplicationScoped
public class OppgaveDtoTjeneste {

    public static final int ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER = 3;

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveDtoTjeneste.class);

    private static final String APPNAVN = Environment.current().getNaisAppName();

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
     */
    public OppgaveDto lagDtoFor(Oppgave oppgave, boolean sjekkTilgangPåBehandling) throws IkkeTilgangPåBehandlingException {
        if (sjekkTilgangPåBehandling) {
            sjekkTilgang(oppgave);
        }
        var person = personTjeneste.hentPerson(oppgave.getAktørId(), String.valueOf(oppgave.getFagsakSaksnummer()))
                .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
        var oppgaveStatus = oppgaveStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, person, oppgaveStatus);
    }

    /**
     * @param oppgave Metoden skal kun brukes ved kall fra endepunkt som tar inn {@link no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto}
     *                eller {@link no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto}. Tilgangssjekk dekkes for slike endepunkt gjennom
     *                {@link no.nav.vedtak.sikkerhet.abac.BeskyttetRessursInterceptor}.
     * @return
     */
    public OppgaveStatusDto lagOppgaveStatusUtenTilgangsjekk(Oppgave oppgave) {
        return oppgaveStatusDtoTjeneste.lagStatusFor(oppgave);
    }

    public boolean finnesTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        return oppgaveKøTjeneste.hentOppgaver(sakslisteId.getVerdi())
                .stream()
                .anyMatch(this::harTilgang);
    }

    private boolean harTilgang(Oppgave oppgave) {
        var token = KontekstHolder.getKontekst() instanceof RequestKontekst rk ? Token.withOidcToken(rk.getToken()) : null;
        if (token == null) {
            return false;
        }
        var dataAttributter = AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVE_ID, oppgave.getId());
        var brRequest = BeskyttetRessursAttributter.builder()
                .medActionType(ActionType.READ)
                .medUserId(KontekstHolder.getKontekst().getUid())
                .medToken(token)
                .medResourceType(ResourceType.FAGSAK)
                .medPepId(APPNAVN)
                .medServicePath(OPPGAVER_BASE_PATH + OPPGAVER_STATUS_PATH)
                .medDataAttributter(dataAttributter);
        var pdpRequest = pdpRequestBuilder.lagAppRessursData(dataAttributter);
        var tilgangsbeslutning = pdpKlient.forespørTilgang(brRequest.build(), pdpRequestBuilder.abacDomene(), pdpRequest);
        return tilgangsbeslutning.fikkTilgang();
    }

    private void sjekkTilgang(Oppgave oppgave) {
        if (!harTilgang(oppgave)) {
            throw new IkkeTilgangPåBehandlingException(oppgave.getBehandlingId());
        }
    }

    public List<OppgaveDto> getOppgaverTilBehandling(Long sakslisteId) {
        var nesteOppgaver = oppgaveKøTjeneste.hentOppgaver(sakslisteId, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER * 10);
        var oppgaveDtos = map(nesteOppgaver, ANTALL_OPPGAVER_SOM_VISES_TIL_SAKSBEHANDLER, true);
        //Noen oppgave filteres bort i mappingen pga at saksbehandler ikke har tilgang til behandlingen
        if (nesteOppgaver.size() == oppgaveDtos.size()) {
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
        return reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()
                .stream()
                .map(o -> tryOrEmpty(() -> lagDtoFor(o, true), "oppgavedto"))
                .flatMap(Optional::stream)
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<OppgaveDto> hentOppgaverForFagsaker(List<Long> saksnummerListe) {
        var oppgaver = oppgaveTjeneste.hentAktiveOppgaverForSaksnummer(saksnummerListe);
        return map(oppgaver);
    }

    private List<OppgaveDto> map(List<Oppgave> oppgaver) {
        return map(oppgaver, oppgaver.size(), false);
    }


    private List<OppgaveDto> map(List<Oppgave> oppgaver, int maksAntall, boolean randomiser) {
        List<OppgaveDto> dtoList = new ArrayList<>();
        var antallOppgaver = oppgaver.size();
        int start = randomiser ? Math.abs((int) (System.nanoTime() % antallOppgaver)) : 0;
        for (var i = 0; i < oppgaver.size() && dtoList.size() < maksAntall; i++) {
            var oppgave = oppgaver.get((start + i) % antallOppgaver);
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
