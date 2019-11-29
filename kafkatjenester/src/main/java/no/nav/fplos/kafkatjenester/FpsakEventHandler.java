package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

@ApplicationScoped
@Transaction
public class FpsakEventHandler extends FpEventHandler {

    private static final Logger log = LoggerFactory.getLogger(FpsakEventHandler.class);

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;

    public FpsakEventHandler(){
        //to make proxyable
    }

    @Inject
    public FpsakEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider,
                             ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient){
        super(oppgaveRepositoryProvider);
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon){
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        Long behandlingId = bpeDto.getBehandlingId();

        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);

        UUID eksternId = behandling.getUuid();

        List<OppgaveEventLogg> pastOppgaveEvents = getOppgaveRepository().hentEventer(behandlingId);
        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        EventResultat event = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                : FpsakEventMapper.signifikantEventFra(aksjonspunkt, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());

        switch (event) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave for behandlingId {} ", behandlingId);
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.LUKKET, null);
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Lukker oppgave ved vent for behandlingId {} ", behandlingId);
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.VENT, finnVentAksjonspunktFrist(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave ved satt manuelt på vent for behandlingId {}", behandlingId);
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppgave {} opprettet og populert med informasjon fra FPSAK for behandlingId {}", oppgave.getId(), behandlingId);
                loggEvent(behandlingId, oppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, oppgave);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave);
                log.info("Oppgave {} opprettet til beslutter og populert med informasjon fra FPSAK for behandlingId {}", beslutterOppgave.getId(), behandlingId);
                getOppgaveRepository().lagre(new OppgaveEgenskap(beslutterOppgave, AndreKriterierType.TIL_BESLUTTER, behandling.getAnsvarligSaksbehandler()));
                loggEvent(behandlingId, beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, beslutterOppgave);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, pastOppgaveEvents, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave);
                log.info("Oppgave {} opprettet fra papirsøknad og populert med informasjon fra FPSAK for behandlingId {}", papirsøknadOppgave.getId(), behandlingId);
                getOppgaveRepository().lagre(new OppgaveEgenskap(papirsøknadOppgave, AndreKriterierType.PAPIRSØKNAD));
                loggEvent(behandlingId, papirsøknadOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, papirsøknadOppgave);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpnetOppgave = gjenåpneOppgave(bpeDto);
                log.info("Gjenåpnet oppgave for behandlingId {}", behandlingId);
                loggEvent(behandlingId, gjenåpnetOppgave.getEksternId(), OppgaveEventType.GJENAPNET, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(behandling, gjenåpnetOppgave);
                break;
        }
    }

    /**
     * @deprecated Bruk avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto, OppgaveEventType, LocalDateTime) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttOppgaveOgLoggEvent(UUID eksternId, BehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime frist){
        //Long eksisterendeEksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), bpeDto.getBehandlingId().toString()).getId();
        avsluttOppgave(bpeDto.getBehandlingId());
        loggEvent(bpeDto.getBehandlingId(), eksternId, eventType, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), frist);
    }

    private void opprettOppgaveEgenskaper(BehandlingFpsak behandling, Oppgave oppgave) {
        håndterOppgaveEgenskapUtbetalingTilBruker(behandling.getHarRefusjonskravFraArbeidsgiver(), oppgave);
        håndterOppgaveEgenskapUtlandssak(behandling.getErUtlandssak(), oppgave);
        håndterOppgaveEgenskapGradering(behandling.getHarGradering(), oppgave);
    }

    private static LocalDateTime finnVentAksjonspunktFrist(List<Aksjonspunkt> aksjonspunktListe) {
        return safeStream(aksjonspunktListe)
                .filter(Aksjonspunkt::erPåVent)
                .map(Aksjonspunkt::getFristTid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime finnManuellAksjonspunktFrist(List<Aksjonspunkt> aksjonspunktListe) {
       return safeStream(aksjonspunktListe)
               .filter(Aksjonspunkt::erManueltPåVent)
               .map(Aksjonspunkt::getFristTid)
               .filter(Objects::nonNull)
               .findFirst()
               .orElse(null);
    }



    private void avsluttOppgaveHvisÅpen(Long behandlingId, UUID eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && OppgaveEventType.åpningseventtyper().contains(oppgaveEventLogger.get(0).getEventType())){
            //Optional<EksternIdentifikator> eksternId = getEksternIdentifikatorRespository().finnIdentifikator(fagsystem, behandlingId.toString());
            if(eksternId != null) {
                loggEvent(behandlingId, eksternId, OppgaveEventType.LUKKET, AndreKriterierType.UKJENT, behandlendeEnhet);
            }
            getOppgaveRepository().avsluttOppgave(behandlingId);
        }
    }

    /**
     * @deprecated Bruk gjenåpneOppgaveVedEksternId(String, String) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private Oppgave gjenåpneOppgave(BehandlingProsessEventDto bpeDto) {
        return getOppgaveRepository().gjenåpneOppgave(bpeDto.getBehandlingId());
    }

    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String) og anvend eksternId
     */
    @Deprecated(since = "14.11.2019")
    private void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        loggEvent(behandlingId, eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, null);
    }


    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String, AksjonspunktDto) og anvend eksternId i stedet for behandlingId
     */
    @Deprecated(since = "14.11.2019")
    protected void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        if (frist != null) {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist, behandlingId));
        } else {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, behandlingId));
        }
    }

    /**
     * @deprecated Bruk avsluttOppgaveForEksternId(Long) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttOppgave(Long behandlingId) {
        getOppgaveRepository().avsluttOppgave(behandlingId);
    }

    private Oppgave opprettOppgave(UUID eksternId, BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        //EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(),bpeDto.getBehandlingId().toString());

        return getOppgaveRepository().opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(getKodeverkRepository().finn(BehandlingType.class, bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(getKodeverkRepository().finn(FagsakYtelseType.class, bpeDto.getYtelseTypeKode()))
                .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                .medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(prosesserFraAdmin)
                .medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                .medBehandlingStatus(getKodeverkRepository().finn(BehandlingStatus.class, fraFpsak.getStatus()))
                .medEksternId(eksternId)
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
            getOppgaveRepository().lagre(eksisterende);
        } else {
            getOppgaveRepository().lagre(new OppgaveEgenskap(oppgave, kriterieType));
        }
    }

    private void deaktiverEgenskap(Oppgave oppgave, AndreKriterierType kriterieType) {
        OppgaveEgenskap eksisterendeEgenskap = hentEksisterendeEgenskap(kriterieType, oppgave);
        if (eksisterendeEgenskap != null) {
            eksisterendeEgenskap.deaktiverOppgaveEgenskap();
            getOppgaveRepository().lagre(eksisterendeEgenskap);
        }
    }

    private OppgaveEgenskap hentEksisterendeEgenskap(AndreKriterierType kriterieType, Oppgave oppgave) {
        List<OppgaveEgenskap> eksisterendeEgenskaper = getOppgaveRepository().hentOppgaveEgenskaper(oppgave.getId());
        return safeStream(eksisterendeEgenskaper)
                .filter(e -> e.getAndreKriterierType().equals(kriterieType))
                .findAny()
                .orElse(null);
    }

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist){
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }
}
