package no.nav.fplos.kafkatjenester;

import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.fplos.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

@ApplicationScoped
@Transaction
public class FpsakEventHandler {

    private static final Logger log = LoggerFactory.getLogger(FpsakEventHandler.class);

    private KodeverkRepository kodeverkRepository;
    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    private static final List<OppgaveEventType> ÅPNINGS_EVENTER = Arrays.asList(OppgaveEventType.OPPRETTET, OppgaveEventType.GJENAPNET);

    public FpsakEventHandler(){
        //to make proxyable
    }

    @Inject
    public FpsakEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider,
                             ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient){
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        this.kodeverkRepository = oppgaveRepositoryProvider.getKodeverkRepository();
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
    }

    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon){
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        Long behandlingId = bpeDto.getBehandlingId();
        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);

        List<OppgaveEventLogg> pastOppgaveEvents = oppgaveRepository.hentEventer(behandlingId);
        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        EventResultat event = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                : FpsakEventMapper.signifikantEventFra(aksjonspunkt, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Lukker oppgave ved vent for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.VENT, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), finnVentAksjonspunkt(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave ved satt manuelt på vent for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), finnManuellAksjonspunkt(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppgave {} opprettet og populert med informasjon fra FPSAK for behandlingId {}", oppgave.getId(), behandlingId);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, oppgave);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettOppgave(bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave);
                log.info("Oppgave {} opprettet til beslutter og populert med informasjon fra FPSAK for behandlingId {}", beslutterOppgave.getId(), behandlingId);
                oppgaveRepository.lagre(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER, behandling.getAnsvarligSaksbehandler()));
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, beslutterOppgave);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = opprettOppgave(bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave);
                log.info("Oppgave {} opprettet fra papirsøknad og populert med informasjon fra FPSAK for behandlingId {}", papirsøknadOppgave.getId(), behandlingId);
                oppgaveRepository.lagre(new OppgaveEgenskap(papirsøknadOppgave, AndreKriterierType.PAPIRSØKNAD));
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, papirsøknadOppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpnetOppgave = gjenåpneOppgave(bpeDto);
                log.info("Gjenåpnet oppgave for behandlingId {}", behandlingId);
                loggEvent(behandlingId, OppgaveEventType.GJENAPNET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, gjenåpnetOppgave);
                break;
        }
    }

    private void opprettOppgaveEgenskaper(BehandlingFpsak behandling, Oppgave oppgave) {
        håndterOppgaveEgenskapUtbetalingTilBruker(behandling.getHarRefusjonskravFraArbeidsgiver(), oppgave);
        håndterOppgaveEgenskapUtlandssak(behandling.getErUtlandssak(), oppgave);
        håndterOppgaveEgenskapGradering(behandling.getHarGradering(), oppgave);
    }

    private static Optional<Aksjonspunkt> finnVentAksjonspunkt(List<Aksjonspunkt> aksjonspunktListe) {
        return aksjonspunktListe.stream()
                .filter(Aksjonspunkt::erPåVent)
                .findFirst();
    }

    private Optional<Aksjonspunkt> finnManuellAksjonspunkt(List<Aksjonspunkt> aksjonspunktListe) {
       return aksjonspunktListe.stream()
               .filter(Aksjonspunkt::erManueltPåVent)
               .findFirst();
    }

    private void reserverOppgaveFraTidligereReservasjon(boolean reserverOppgave,
                                                        Reservasjon reservasjon,
                                                        Oppgave oppgave) {
        if (reserverOppgave && reservasjon != null) {
            oppgaveRepository.reserverOppgaveFraTidligereReservasjon(oppgave.getId(), reservasjon);
        }
    }

    private void avsluttOppgaveHvisÅpen(Long behandlingId,
                                        List<OppgaveEventLogg> oppgaveEventLogger,
                                        String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && ÅPNINGS_EVENTER.contains(oppgaveEventLogger.get(0).getEventType())){
            loggEvent(behandlingId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, behandlendeEnhet);
            oppgaveRepository.avsluttOppgave(behandlingId);
        }
    }

    private Oppgave gjenåpneOppgave(BehandlingProsessEventDto bpeDto) {
        return oppgaveRepository.gjenåpneOppgave(bpeDto.getBehandlingId());
    }

    private void loggEvent(Long behandlingId,
                           OppgaveEventType oppgaveEventType,
                           AndreKriterierType andreKriterierType,
                           String behandlendeEnhet) {
        loggEvent(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet, Optional.empty());
    }

    private void loggEvent(Long behandlingId,
                           OppgaveEventType oppgaveEventType,
                           AndreKriterierType andreKriterierType,
                           String behandlendeEnhet,
                           Optional<Aksjonspunkt> aksjonspunktDto) {
        if (aksjonspunktDto.isPresent() && aksjonspunktDto.get().getFristTid() != null) {
            oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType,
                    andreKriterierType, behandlendeEnhet, aksjonspunktDto.get().getFristTid()));
        } else {
            oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType,
                    andreKriterierType, behandlendeEnhet));
        }
    }

    private void avsluttOppgave(Long behandlingId) {
        oppgaveRepository.avsluttOppgave(behandlingId);
    }

    private Oppgave opprettOppgave(BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        return oppgaveRepository.opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(kodeverkRepository.finn(BehandlingType.class, bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(kodeverkRepository.finn(FagsakYtelseType.class, bpeDto.getYtelseTypeKode()))
                .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                .medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(prosesserFraAdmin)
                .medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                .medBehandlingStatus(kodeverkRepository.finn(BehandlingStatus.class, fraFpsak.getStatus()))
                .build());
    }

    public void håndterOppgaveEgenskapUtbetalingTilBruker(Boolean harRefusjonskrav, Oppgave oppgave) {
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        boolean skalHaEgenskap = harRefusjonskrav != null && !harRefusjonskrav;
        håndterOppgaveEgenskap(skalHaEgenskap, oppgave, AndreKriterierType.UTBETALING_TIL_BRUKER);
    }

    public void håndterOppgaveEgenskapUtlandssak(boolean erUtlandsak, Oppgave oppgave) {
        håndterOppgaveEgenskap(erUtlandsak, oppgave, AndreKriterierType.UTLANDSSAK);
    }

    public void håndterOppgaveEgenskapGradering(Boolean harGradering, Oppgave oppgave) {
        håndterOppgaveEgenskap(harGradering, oppgave, AndreKriterierType.SOKT_GRADERING);
    }

    private void håndterOppgaveEgenskap(Boolean status, Oppgave oppgave, AndreKriterierType kriterieType) {
        if (status != null && status) {
            aktiverEgenskap(oppgave, kriterieType);
        } else {
            deaktiverEgenskap(oppgave, kriterieType);
        }
    }

    private void aktiverEgenskap(Oppgave oppgave, AndreKriterierType kriterieType) {
        OppgaveEgenskap eksisterende = hentEksisterendeEgenskap(kriterieType, oppgave);
        if (eksisterende != null) {
            eksisterende.aktiverOppgaveEgenskap();
            oppgaveRepository.lagre(eksisterende);
        } else {
            oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, kriterieType));
        }
    }

    private void deaktiverEgenskap(Oppgave oppgave, AndreKriterierType kriterieType) {
        OppgaveEgenskap eksisterendeEgenskap = hentEksisterendeEgenskap(kriterieType, oppgave);
        if (eksisterendeEgenskap != null) {
            eksisterendeEgenskap.deaktiverOppgaveEgenskap();
            oppgaveRepository.lagre(eksisterendeEgenskap);
        }
    }

    private OppgaveEgenskap hentEksisterendeEgenskap(AndreKriterierType kriterieType, Oppgave oppgave) {
        List<OppgaveEgenskap> eksisterendeEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId());
        return safeStream(eksisterendeEgenskaper)
                .filter(e -> e.getAndreKriterierType().equals(kriterieType))
                .findAny()
                .orElse(null);
    }

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist){
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }
}
