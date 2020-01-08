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
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
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
    public FpsakEventHandler(OppgaveRepository oppgaveRepository,
                             ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient){
        super(oppgaveRepository);
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto) {
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon) {
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        Long behandlingId = bpeDto.getBehandlingId();

        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        UUID eksternId = behandling.getUuid();

        List<OppgaveEventLogg> tidligereEventer = getOppgaveRepository().hentEventerForEksternId(eksternId);
        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        FpsakEvent event = new FpsakEvent(behandling, tidligereEventer, aksjonspunkt);

        EventResultat eventResultat = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                : FpsakEventMapper.signifikantEventFra(aksjonspunkt, tidligereEventer, bpeDto.getBehandlendeEnhet());

        switch (eventResultat) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave");
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.LUKKET, null);
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.VENT, finnVentAksjonspunktFrist(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId, tidligereEventer, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave);
                log.info("Oppretter oppgave");
                loggEvent(oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(oppgave, event);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId, tidligereEventer, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave);
                loggEvent(beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(beslutterOppgave, event);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(eksternId, tidligereEventer, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = opprettOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave);
                loggEvent(papirsøknadOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(papirsøknadOppgave, event);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpneOppgave = gjenåpneOppgaveVedEksternId(eksternId);
                log.info("Gjenåpner oppgave");
                loggEvent(gjenåpneOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, bpeDto.getBehandlendeEnhet());
                opprettOppgaveEgenskaper(gjenåpneOppgave, event);
                break;
        }
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, FpsakEvent event) {
        var andreKriterier = event.getAndreKriterier();
        log.info("Legger på oppgaveegenskaper {}", andreKriterier);
        List<OppgaveEgenskap> eksisterende = hentEksisterendeEgenskaper(oppgave);

        // deaktiver uaktuelle eksisterende
        eksisterende.stream()
                .filter(akt -> !andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(oe -> {
                    oe.deaktiverOppgaveEgenskap();
                    getOppgaveRepository().lagre(oe);
                });

        // aktiver aktuelle eksisterende
        eksisterende.stream()
                .filter(akt -> andreKriterier.contains(akt.getAndreKriterierType()))
                .forEach(oe -> aktiverEksisterendeEgenskaper(oe, event.getSaksbehandlerForTotrinn()));

        // aktiver nye
        andreKriterier.stream()
                .filter(akt -> !eksisterende.stream()
                        .map(OppgaveEgenskap::getAndreKriterierType)
                        .equals(akt))
                .forEach(k -> opprettOppgaveEgenskaper(oppgave, k, event.getSaksbehandlerForTotrinn()));
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, AndreKriterierType kritere, String saksbehandler) {
        if (kritere.equals(AndreKriterierType.TIL_BESLUTTER)) {
            getOppgaveRepository().lagre(new OppgaveEgenskap(oppgave, kritere, saksbehandler));
        } else {
            getOppgaveRepository().lagre(new OppgaveEgenskap(oppgave, kritere));
        }
    }

    private void aktiverEksisterendeEgenskaper(OppgaveEgenskap oppgaveEgenskap, String saksbehandler) {
        if (oppgaveEgenskap.getAndreKriterierType().equals(AndreKriterierType.TIL_BESLUTTER)) {
            oppgaveEgenskap.aktiverOppgaveEgenskap();
            oppgaveEgenskap.setSisteSaksbehandlerForTotrinn(saksbehandler);
            getOppgaveRepository().lagre(oppgaveEgenskap);
        } else {
            oppgaveEgenskap.aktiverOppgaveEgenskap();
            getOppgaveRepository().lagre(oppgaveEgenskap);
        }
    }

    /**
     * @deprecated Bruk avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto, OppgaveEventType, LocalDateTime) i stedet
     */
    /*@Deprecated(since = "14.11.2019")
    private void avsluttOppgaveOgLoggEvent(UUID eksternId, BehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime frist){
        //Long eksisterendeEksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), bpeDto.getBehandlingId().toString()).getId();
        avsluttOppgave(bpeDto.getBehandlingId());
        loggEvent(bpeDto.getBehandlingId(), eksternId, eventType, null, bpeDto.getBehandlendeEnhet(), frist);
    }*/

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

//    private static boolean finnSelvstendigFrilans(List<Aksjonspunkt> aksjonspunktListe) {
//        return safeStream(aksjonspunktListe)
//                .anyMatch(Aksjonspunkt::erSelvstendigEllerFrilanser);
//    }

    private void avsluttOppgaveHvisÅpen(UUID eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && oppgaveEventLogger.get(0).getEventType().erÅpningsevent()){
            //Optional<EksternIdentifikator> eksternId = getEksternIdentifikatorRespository().finnIdentifikator(fagsystem, behandlingId.toString());
            if(eksternId != null) {
                loggEvent(eksternId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            }
            getOppgaveRepository().avsluttOppgaveForEksternId(eksternId);
        }
    }

    /**
     * @deprecated Bruk gjenåpneOppgaveVedEksternId(String, String) i stedet
     */
    /*@Deprecated(since = "14.11.2019")
    private Oppgave gjenåpneOppgave(BehandlingProsessEventDto bpeDto) {
        return getOppgaveRepository().gjenåpneOppgave(bpeDto.getBehandlingId());
    }*/

    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String) og anvend eksternId
     */
    /*@Deprecated(since = "14.11.2019")
    private void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        loggEvent(behandlingId, eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, null);
    }*/


    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String, AksjonspunktDto) og anvend eksternId i stedet for behandlingId
     */
    /*@Deprecated(since = "14.11.2019")
    protected void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        if (frist != null) {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist, behandlingId));
        } else {
            getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, behandlingId));
        }
    }*/

    /**
     * @deprecated Bruk avsluttOppgaveForEksternId(Long) i stedet
     */
    /*@Deprecated(since = "14.11.2019")
    private void avsluttOppgave(Long behandlingId) {
        getOppgaveRepository().avsluttOppgave(behandlingId);
    }
*/
    private Oppgave opprettOppgave(UUID eksternId, BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        //EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(),bpeDto.getBehandlingId().toString());

        return getOppgaveRepository().opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(bpeDto.getYtelseTypeKode()))
                .medAktiv(true).medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
                .medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(prosesserFraAdmin)
                .medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                .medBehandlingStatus(BehandlingStatus.fraKode(fraFpsak.getStatus()))
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

    private List<OppgaveEgenskap> hentEksisterendeEgenskaper(Oppgave oppgave) {
        return Optional.ofNullable(getOppgaveRepository().hentOppgaveEgenskaper(oppgave.getId()))
                .orElse(Collections.emptyList());
    }

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist){
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }
}
