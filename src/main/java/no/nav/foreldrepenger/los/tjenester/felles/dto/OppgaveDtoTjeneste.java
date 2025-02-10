package no.nav.foreldrepenger.los.tjenester.felles.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.persontjeneste.IkkeTilgangPåPersonException;
import no.nav.foreldrepenger.los.persontjeneste.PersonTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.BehandlingIdDto;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.sikkerhet.populasjon.TilgangKlient;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.Token;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.AvailabilityType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;
import no.nav.vedtak.sikkerhet.abac.internal.BeskyttetRessursAttributter;
import no.nav.vedtak.sikkerhet.abac.policy.InternBrukerPolicies;
import no.nav.vedtak.sikkerhet.kontekst.AnsattGruppe;
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
    private ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste;
    private TilgangKlient tilgangKlient;
    private PdpRequestBuilder pdpRequestBuilder;
    private OppgaveKøTjeneste oppgaveKøTjeneste;

    @Inject
    public OppgaveDtoTjeneste(OppgaveTjeneste oppgaveTjeneste,
                              ReservasjonTjeneste reservasjonTjeneste,
                              PersonTjeneste personTjeneste,
                              ReservasjonStatusDtoTjeneste reservasjonStatusDtoTjeneste,
                              TilgangKlient tilgangKlient,
                              PdpRequestBuilder pdpRequestBuilder,
                              OppgaveKøTjeneste oppgaveKøTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.personTjeneste = personTjeneste;
        this.reservasjonStatusDtoTjeneste = reservasjonStatusDtoTjeneste;
        this.tilgangKlient = tilgangKlient;
        this.pdpRequestBuilder = pdpRequestBuilder;
        this.oppgaveKøTjeneste = oppgaveKøTjeneste;
    }

    OppgaveDtoTjeneste() {
        //CDI
    }

    /**
     * @param sjekkTilgangPåBehandling Ved alle kall som tar i mot {@link BehandlingIdDto}
     *                                 eller {@link OppgaveIdDto}
     *                                 sjekkes tilgangen til behandlingen/oppgaven ved mottaket av restkallet gjennom {@link no.nav.vedtak.sikkerhet.abac.BeskyttetRessursInterceptor}.
     *                                 Applikasjonen har noen kall som ikke går rett på en behandling/oppgave, men som returnerer oppgaver,
     *                                 i disse tilfellene må sjekkTilgangPåBehandling være true.
     */
    public OppgaveDto lagDtoFor(Oppgave oppgave, boolean sjekkTilgangPåBehandling) throws IkkeTilgangPåBehandlingException {
        if (sjekkTilgangPåBehandling) {
            sjekkTilgang(oppgave);
        }
        var person = personTjeneste.hentPerson(oppgave.getFagsakYtelseType(), oppgave.getAktørId(), String.valueOf(oppgave.getFagsakSaksnummer()))
            .orElseThrow(() -> new LagOppgaveDtoFeil("Finner ikke person tilknyttet oppgaveId " + oppgave.getId()));
        var oppgaveStatus = reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
        return new OppgaveDto(oppgave, person, oppgaveStatus);
    }

    /**
     * @param oppgave Metoden skal kun brukes ved kall fra endepunkt som tar inn {@link BehandlingIdDto}
     *                eller {@link OppgaveIdDto}. Tilgangssjekk dekkes for slike endepunkt gjennom
     *                {@link no.nav.vedtak.sikkerhet.abac.BeskyttetRessursInterceptor}.
     * @return
     */
    public ReservasjonStatusDto lagOppgaveStatusUtenTilgangsjekk(Oppgave oppgave) {
        return reservasjonStatusDtoTjeneste.lagStatusFor(oppgave);
    }

    public boolean finnesTilgjengeligeOppgaver(SakslisteIdDto sakslisteId) {
        return oppgaveKøTjeneste.hentOppgaver(sakslisteId.getVerdi()).stream().anyMatch(this::harTilgang);
    }

    private boolean harTilgang(Oppgave oppgave) {
        var kontekst = KontekstHolder.getKontekst() instanceof RequestKontekst rk ? rk : null;
        if (kontekst == null) {
            return false;
        }
        var dataAttributter = AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVE_ID, oppgave.getId());
        var brRequest = BeskyttetRessursAttributter.builder()
            .medActionType(ActionType.READ)
            .medBrukerId(kontekst.getUid())
            .medBrukerOid(kontekst.getOid())
            .medIdentType(kontekst.getIdentType())
            .medAnsattGrupper(kontekst.getGrupper())
            .medAvailabilityType(AvailabilityType.INTERNAL)
            .medSporingslogg(false)
            .medToken(Token.withOidcToken(kontekst.getToken()))
            .medResourceType(ResourceType.FAGSAK)
            .medPepId(APPNAVN)
            .medServicePath(OppgaveRestTjeneste.OPPGAVER_BASE_PATH + OppgaveRestTjeneste.OPPGAVER_STATUS_PATH)
            .medDataAttributter(dataAttributter)
            .build();
        var pdpRequest = pdpRequestBuilder.lagAppRessursData(dataAttributter);
        // Vurdering av populasjonstilgang
        if (pdpRequest.getFødselsnumre().isEmpty() && pdpRequest.getAktørIdSet().isEmpty()) {
            // Ikke noe å sjekke for populasjonstilgang
            return true;
        }
        var popTilgang = tilgangKlient.vurderTilgangInternBruker(brRequest.getBrukerOid(),
            pdpRequest.getFødselsnumre(), pdpRequest.getAktørIdSet());
        return popTilgang != null && popTilgang.fikkTilgang();
    }

    private static boolean harGruppe(BeskyttetRessursAttributter beskyttetRessursAttributter, AnsattGruppe gruppe) {
        return beskyttetRessursAttributter.getAnsattGrupper().stream().anyMatch(gruppe::equals);
    }

    private void sjekkTilgang(Oppgave oppgave) {
        if (!harTilgang(oppgave)) {
            throw new IkkeTilgangPåBehandlingException(oppgave.getBehandlingId());
        }
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
        return reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()
            .stream()
            .map(o -> safeLagDtoFor(o, true))
            .flatMap(Optional::stream)
            .limit(10)
            .toList();
    }

    private Optional<OppgaveDto> safeLagDtoFor(Oppgave oppgave, boolean sjekkTilgangPåBehandling) {
        try {
            return Optional.of(lagDtoFor(oppgave, sjekkTilgangPåBehandling));
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
        List<OppgaveDto> dtoList = new ArrayList<>();
        var antallOppgaver = oppgaver.size();
        int start = randomiser ? (int)(System.currentTimeMillis() % antallOppgaver) : 0;
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
