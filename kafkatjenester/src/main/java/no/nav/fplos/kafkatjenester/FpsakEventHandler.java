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
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

@ApplicationScoped
@Transaction
public class FpsakEventHandler {

    private static final Logger log = LoggerFactory.getLogger(FpsakEventHandler.class);

    private KodeverkRepository kodeverkRepository;
    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    private static final List<String> aktiveAksjonspunktkoder = Collections.singletonList("OPPR");
    private static final List<String> avbruttAksjonspunktkoder = Collections.singletonList("AVBR");

    private static final List<OppgaveEventType> ÅPNINGS_EVENTER = Arrays.asList(OppgaveEventType.OPPRETTET, OppgaveEventType.GJENAPNET);

    public FpsakEventHandler(){
        //to make poroxyable
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
        BehandlingFpsak fraFpsak = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);

        List<OppgaveEventLogg> pastOppgaveEvents = oppgaveRepository.hentEventer(behandlingId);
        List<AksjonspunktDto> aksjonspunktListe = new ArrayList<>();
        fraFpsak.getAksjonspunkter().forEach(aksjonspunkt -> aksjonspunktListe.add(aksjonspunkt));

        EventResultat event = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunktListe)
                : FpsakEventMapper.signifikantEventFra(aksjonspunktListe, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Lukker oppgave ved vent for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.VENT, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), finnVentAksjonspunkt(aksjonspunktListe));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave ved satt manuelt på vent for behandlingId {}", behandlingId);
                avsluttOppgave(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), finnManuellAksjonspunkt(aksjonspunktListe));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppgave {} opprettet og populert med informasjon fra FPSAK for behandlingId {}", oppgave.getId(), behandlingId);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, oppgave);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave);
                log.info("Oppgave {} opprettet til beslutter og populert med informasjon fra FPSAK for behandlingId {}", beslutterOppgave.getId(), behandlingId);
                oppgaveRepository.lagre(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER, fraFpsak.getAnsvarligSaksbehandler()));
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, beslutterOppgave);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave);
                log.info("Oppgave {} opprettet fra papirsøknad og populert med informasjon fra FPSAK for behandlingId {}", papirsøknadOppgave.getId(), behandlingId);
                oppgaveRepository.lagre(new OppgaveEgenskap(papirsøknadOppgave, AndreKriterierType.PAPIRSØKNAD));
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, papirsøknadOppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpnetOppgave = gjenåpneOppgave(bpeDto);
                log.info("Gjenåpnet oppgave for behandlingId {}", behandlingId);
                loggEvent(behandlingId, OppgaveEventType.GJENAPNET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, gjenåpnetOppgave);
                break;
        }
    }

    private void opprettOppgaveEgenskaper(BehandlingFpsak behandling, List<AksjonspunktDto> aksjonspunktListe, Oppgave oppgave) {
        håndterOppgaveEgenskapUtbetalingTilBruker(behandling.getHarRefusjonskravFraArbeidsgiver(), oppgave);
        håndterOppgaveEgenskapUtlandssak(avgjørOmUtlandsak(aksjonspunktListe, behandling.getErUtlandssak()), oppgave);
        håndterOppgaveEgenskapGradering(behandling.getHarGradering(), oppgave);
    }

    private Optional<AksjonspunktDto> finnVentAksjonspunkt(List<AksjonspunktDto> aksjonspunktListe) {
        return aksjonspunktListe.stream().filter(aksjonspunkt -> aksjonspunkt.getDefinisjon().getKode().startsWith("7")
                && aktiveAksjonspunktkoder.contains(aksjonspunkt.getStatus().getKode())).findFirst();
    }

    private Optional<AksjonspunktDto> finnManuellAksjonspunkt(List<AksjonspunktDto> aksjonspunktListe) {
       return aksjonspunktListe.stream().filter(entry -> FpsakEventMapper.MANUELT_SATT_PÅ_VENT_AKSJONSPUNKTSKODE.equals(entry.getDefinisjon().getKode())
               && aktiveAksjonspunktkoder.contains(entry.getStatus().getKode())).findFirst();
    }

    private void reserverOppgaveFraTidligereReservasjon(boolean reserverOppgave, Reservasjon reservasjon, Oppgave oppgave) {
        if (reserverOppgave && reservasjon != null) {
            oppgaveRepository.reserverOppgaveFraTidligereReservasjon(oppgave.getId(), reservasjon);
        }
    }

    private void avsluttOppgaveHvisÅpen(Long behandlingId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && ÅPNINGS_EVENTER.contains(oppgaveEventLogger.get(0).getEventType())){
            loggEvent(behandlingId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, behandlendeEnhet);
            oppgaveRepository.avsluttOppgave(behandlingId);
        }
    }

    private Oppgave gjenåpneOppgave(BehandlingProsessEventDto bpeDto) {
        return oppgaveRepository.gjenåpneOppgave(bpeDto.getBehandlingId());
    }

    private void loggEvent(Long behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        loggEvent(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet, Optional.empty());
    }

    private void loggEvent(Long behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, Optional<AksjonspunktDto> aksjonspunktDto) {
        if (aksjonspunktDto.isPresent() && aksjonspunktDto.get().getFristTid() != null) {
            oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet, aksjonspunktDto.get().getFristTid()));
        } else {
            oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
        }
    }

    private void avsluttOppgave(Long behandlingId) {
        oppgaveRepository.avsluttOppgave(behandlingId);
    }

    private Oppgave opprettOppgave(BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        return oppgaveRepository.opprettOppgave(Oppgave.builder()
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

    public Boolean avgjørOmUtlandsak(List<AksjonspunktDto> aksjonspunktKoderMedStatusListe, Boolean erMarkertManueltSomUtlandssak) {
        Set<AksjonspunktDto> aksjonspunkter = aksjonspunktKoderMedStatusListe.stream()
                .filter(entry -> !avbruttAksjonspunktkoder.contains(entry.getDefinisjon().getKode())).collect(Collectors.toSet());
        boolean erAutomatiskMarkertSomUtlandssak = aksjonspunkter.stream().anyMatch(entry -> FpsakEventMapper.AUTOMATISK_MARKERING_AV_UTENLANDSSAK_AKSJONSPUNKTSKODE.contains(entry.getDefinisjon().getKode()));
        boolean erManueltMarkertSomUtlandssak = erMarkertManueltSomUtlandssak != null ? erMarkertManueltSomUtlandssak : false;
        return erAutomatiskMarkertSomUtlandssak || erManueltMarkertSomUtlandssak;
    }

    public void håndterOppgaveEgenskapUtbetalingTilBruker(Boolean harRefusjonskrav, Oppgave oppgave) {
        List<OppgaveEgenskap> oppgaveEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId());
        OppgaveEgenskap kriterieTilknyttetOppgave = eksisterendeOppgaveEgenskapForKriterium(oppgaveEgenskaper, AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (harRefusjonskrav != null && !harRefusjonskrav) {
            aktiverEllerLeggTilOppgaveEgenskap(kriterieTilknyttetOppgave, oppgave, AndreKriterierType.UTBETALING_TIL_BRUKER);
        } else {
            deaktiverOppgaveEgenskap(kriterieTilknyttetOppgave);
        }
    }

    public void håndterOppgaveEgenskapUtlandssak(Boolean erUtlandsak, Oppgave oppgave) {
        List<OppgaveEgenskap> oppgaveEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId());
        OppgaveEgenskap kriterieTilknyttetOppgave = eksisterendeOppgaveEgenskapForKriterium(oppgaveEgenskaper, AndreKriterierType.UTLANDSSAK);
        if (erUtlandsak != null && erUtlandsak) {
            aktiverEllerLeggTilOppgaveEgenskap(kriterieTilknyttetOppgave, oppgave, AndreKriterierType.UTLANDSSAK);
        } else {
            deaktiverOppgaveEgenskap(kriterieTilknyttetOppgave);
        }
    }

    public void håndterOppgaveEgenskapGradering(Boolean harGradering, Oppgave oppgave) {
        List<OppgaveEgenskap> oppgaveEgenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId());
        OppgaveEgenskap eksisterendeEgenskap = eksisterendeOppgaveEgenskapForKriterium(oppgaveEgenskaper, AndreKriterierType.SOKT_GRADERING);
        if (harGradering != null && harGradering) {
            aktiverEllerLeggTilOppgaveEgenskap(eksisterendeEgenskap, oppgave, AndreKriterierType.SOKT_GRADERING);
        } else {
            deaktiverOppgaveEgenskap(eksisterendeEgenskap);
        }
    }

    private void aktiverEllerLeggTilOppgaveEgenskap(OppgaveEgenskap kriterieTilknyttetOppgave, Oppgave oppgave, AndreKriterierType andreKriterierType) {
        if (kriterieTilknyttetOppgave != null) {
            kriterieTilknyttetOppgave.aktiverOppgaveEgenskap();
            oppgaveRepository.lagre(kriterieTilknyttetOppgave);
        } else {
            oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, andreKriterierType));
        }
    }

    private void deaktiverOppgaveEgenskap(OppgaveEgenskap kriterieTilknyttetOppgave) {
        if (kriterieTilknyttetOppgave != null) {
            kriterieTilknyttetOppgave.deaktiverOppgaveEgenskap();
            oppgaveRepository.lagre(kriterieTilknyttetOppgave);
        }
    }

    private static OppgaveEgenskap eksisterendeOppgaveEgenskapForKriterium(List<OppgaveEgenskap> oppgaveEgenskaper, AndreKriterierType targetKriterium) {
        return safeStream(oppgaveEgenskaper)
                .filter(e -> e.getAndreKriterierType().equals(targetKriterium))
                .findAny()
                .orElse(null);
    }

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist){
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }
}