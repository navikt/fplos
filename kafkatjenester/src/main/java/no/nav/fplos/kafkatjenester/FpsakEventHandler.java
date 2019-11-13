package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.EksternIdentifikator;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
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
public class FpsakEventHandler extends FpEventHandler {

    private static final Logger log = LoggerFactory.getLogger(FpsakEventHandler.class);

    private KodeverkRepository kodeverkRepository;

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
        super(oppgaveRepositoryProvider);
        this.kodeverkRepository = oppgaveRepositoryProvider.getKodeverkRepository();
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon){
        prosesser(bpeDto, reservasjon, true);
    }

    /**
     * @deprecated Bruk hentEventerVedEksternId(String, String) og benytt eksternRefId( finnes som bpeDto.getId() ) i stedet
     */
    @Deprecated
    private List<OppgaveEventLogg> hentEventer(String fagsystem, Long behandlingId) {
        return getOppgaveRepository().hentEventer(behandlingId);
    }

    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        Long behandlingId = bpeDto.getBehandlingId();
        BehandlingFpsak fraFpsak = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);

        List<OppgaveEventLogg> pastOppgaveEvents = hentEventer(bpeDto.getFagsystem(), behandlingId);

        List<AksjonspunktDto> aksjonspunktListe = new ArrayList<>();
        fraFpsak.getAksjonspunkter().forEach(aksjonspunkt -> aksjonspunktListe.add(aksjonspunkt));

        EventResultat event = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunktListe)
                : FpsakEventMapper.signifikantEventFra(aksjonspunktListe, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave for behandlingId {} ", behandlingId);
                avsluttOppgaveOgLoggEvent(bpeDto, OppgaveEventType.LUKKET, Optional.empty());
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Lukker oppgave ved vent for behandlingId {} ", behandlingId);
                avsluttOppgaveOgLoggEvent(bpeDto, OppgaveEventType.VENT, finnVentAksjonspunkt(aksjonspunktListe));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave ved satt manuelt på vent for behandlingId {}", behandlingId);
                avsluttOppgaveOgLoggEvent(bpeDto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunkt(aksjonspunktListe));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, bpeDto.getFagsystem(), pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppgave {} opprettet og populert med informasjon fra FPSAK for behandlingId {}", oppgave.getId(), behandlingId);
                loggEvent(behandlingId, oppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, oppgave);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, bpeDto.getFagsystem(), pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave);
                log.info("Oppgave {} opprettet til beslutter og populert med informasjon fra FPSAK for behandlingId {}", beslutterOppgave.getId(), behandlingId);
                getOppgaveRepository().lagre(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER, fraFpsak.getAnsvarligSaksbehandler()));
                loggEvent(behandlingId, beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, beslutterOppgave);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, bpeDto.getFagsystem(), pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = opprettOppgave(bpeDto, fraFpsak, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave);
                log.info("Oppgave {} opprettet fra papirsøknad og populert med informasjon fra FPSAK for behandlingId {}", papirsøknadOppgave.getId(), behandlingId);
                getOppgaveRepository().lagre(new OppgaveEgenskap(papirsøknadOppgave, AndreKriterierType.PAPIRSØKNAD));
                loggEvent(behandlingId, papirsøknadOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, papirsøknadOppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpnetOppgave = gjenåpneOppgave(bpeDto);
                log.info("Gjenåpnet oppgave for behandlingId {}", behandlingId);
                loggEvent(behandlingId, gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(fraFpsak, aksjonspunktListe, gjenåpnetOppgave);
                break;
        }
    }

    /**
     *
     * @deprecated Bruk avsluttOppgaveOgLoggEvent(BehandlingProsessEventDto, OppgaveEventType, AksjonspunktDto) i stedet
     */
    @Deprecated
    private void avsluttOppgaveOgLoggEvent(BehandlingProsessEventDto bpeDto, OppgaveEventType eventType, Optional<AksjonspunktDto> aksjonspunkt){
        Long eksisterendeEksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), bpeDto.getBehandlingId().toString()).getId();
        avsluttOppgave(bpeDto.getBehandlingId());
        loggEvent(bpeDto.getBehandlingId(), eksisterendeEksternId, eventType, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), aksjonspunkt);
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
            getOppgaveRepository().reserverOppgaveFraTidligereReservasjon(oppgave.getId(), reservasjon);
        }
    }

    private void avsluttOppgaveHvisÅpen(Long behandlingId, String fagsystem, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && ÅPNINGS_EVENTER.contains(oppgaveEventLogger.get(0).getEventType())){
            Optional<EksternIdentifikator> eksternId = getEksternIdentifikatorRespository().finnIdentifikator(fagsystem, behandlingId.toString());
            if(eksternId.isPresent()) {
                loggEvent(behandlingId, eksternId.get().getId(), OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, behandlendeEnhet);
            }
            getOppgaveRepository().avsluttOppgave(behandlingId);
        }
    }

    /**
     * @deprecated Bruk gjenåpneOppgaveForEksternId(BehandlingProsessEventDto) i stedet
     */
    @Deprecated
    private Oppgave gjenåpneOppgave(BehandlingProsessEventDto bpeDto) {
        return getOppgaveRepository().gjenåpneOppgave(bpeDto.getBehandlingId());
    }

    /**
     *
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String) og anvend eksternId
     */
    @Deprecated
    private void loggEvent(Long behandlingId, Long eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        loggEvent(behandlingId, eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, Optional.empty());
    }


    /**
     *
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String, AksjonspunktDto) og anvend eksternId i stedet for behandlingId
     */
    @Deprecated
    protected void loggEvent(Long behandlingId, Long eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, Optional<AksjonspunktDto> aksjonspunktDto) {
        if (aksjonspunktDto.isPresent() && aksjonspunktDto.get().getFristTid() != null) {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, aksjonspunktDto.get().getFristTid(), behandlingId));
        } else {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, behandlingId));
        }
    }


    /**
     *
     * @deprecated Bruk avsluttOppgaveForEksternId(Long) i stedet
     */
    @Deprecated
    private void avsluttOppgave(Long behandlingId) {
        getOppgaveRepository().avsluttOppgave(behandlingId);
    }

    private Oppgave opprettOppgave(BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(),bpeDto.getBehandlingId().toString());

        return getOppgaveRepository().opprettOppgave(Oppgave.builder()
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
                .medEksternId(eksternId.getId())
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
        List<OppgaveEgenskap> oppgaveEgenskaper = getOppgaveRepository().hentOppgaveEgenskaper(oppgave.getId());
        OppgaveEgenskap kriterieTilknyttetOppgave = eksisterendeOppgaveEgenskapForKriterium(oppgaveEgenskaper, AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (harRefusjonskrav != null && !harRefusjonskrav) {
            aktiverEllerLeggTilOppgaveEgenskap(kriterieTilknyttetOppgave, oppgave, AndreKriterierType.UTBETALING_TIL_BRUKER);
        } else {
            deaktiverOppgaveEgenskap(kriterieTilknyttetOppgave);
        }
    }

    public void håndterOppgaveEgenskapUtlandssak(Boolean erUtlandsak, Oppgave oppgave) {
        List<OppgaveEgenskap> oppgaveEgenskaper = getOppgaveRepository().hentOppgaveEgenskaper(oppgave.getId());
        OppgaveEgenskap kriterieTilknyttetOppgave = eksisterendeOppgaveEgenskapForKriterium(oppgaveEgenskaper, AndreKriterierType.UTLANDSSAK);
        if (erUtlandsak != null && erUtlandsak) {
            aktiverEllerLeggTilOppgaveEgenskap(kriterieTilknyttetOppgave, oppgave, AndreKriterierType.UTLANDSSAK);
        } else {
            deaktiverOppgaveEgenskap(kriterieTilknyttetOppgave);
        }
    }

    public void håndterOppgaveEgenskapGradering(Boolean harGradering, Oppgave oppgave) {
        List<OppgaveEgenskap> oppgaveEgenskaper = getOppgaveRepository().hentOppgaveEgenskaper(oppgave.getId());
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
            getOppgaveRepository().lagre(kriterieTilknyttetOppgave);
        } else {
            getOppgaveRepository().lagre(new OppgaveEgenskap(oppgave, andreKriterierType));
        }
    }

    private void deaktiverOppgaveEgenskap(OppgaveEgenskap kriterieTilknyttetOppgave) {
        if (kriterieTilknyttetOppgave != null) {
            kriterieTilknyttetOppgave.deaktiverOppgaveEgenskap();
            getOppgaveRepository().lagre(kriterieTilknyttetOppgave);
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