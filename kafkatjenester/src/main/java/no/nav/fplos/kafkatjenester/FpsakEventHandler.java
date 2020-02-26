package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
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
public class FpsakEventHandler extends FpEventHandler<FpsakBehandlingProsessEventDto> {

    private static final Logger log = LoggerFactory.getLogger(FpsakEventHandler.class);
    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;

    public FpsakEventHandler() {
        //to make proxyable
    }

    @Inject
    public FpsakEventHandler(OppgaveRepository oppgaveRepository,
                             ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient,
                             OppgaveEgenskapHandler oppgaveEgenskapHandler) {
        super(oppgaveRepository);
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
    }

    @Override
    public void prosesser(FpsakBehandlingProsessEventDto bpeDto) {
        prosesser(bpeDto, null,false);
    }

    public void prosesserFraAdmin(FpsakBehandlingProsessEventDto bpeDto, Reservasjon reservasjon) {
        prosesser(bpeDto, reservasjon, true);
    }

    private void prosesser(FpsakBehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        Long behandlingId = bpeDto.getBehandlingId();
        UUID eksternId = bpeDto.getEksternId();
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(getOppgaveRepository().hentOppgaveEventer(eksternId));

        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        if(eksternId == null) {
            eksternId = behandling.getUuid();
        }

        //OppgaveEventLogg sisteOppgaveEvent = getOppgaveRepository().hentSisteOpprettelseEvent(eksternId);
        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        OppgaveEgenskapFinner egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling, aksjonspunkt);

        EventResultat eventResultat = prosesserFraAdmin
                ? FpsakEventMapper.signifikantEventForAdminFra(aksjonspunkt)
                : FpsakEventMapper.signifikantEventFra(aksjonspunkt, oppgaveHistorikk, bpeDto.getBehandlendeEnhet());

        switch (eventResultat) {
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.LUKKET, null);
                break;
            case LUKK_OPPGAVE_VENT:
                log.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.VENT, finnVentAksjonspunktFrist(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, bpeDto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, bpeDto.getBehandlendeEnhet());
                Oppgave oppgave = nyOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, oppgave.getId());
                log.info("Oppretter oppgave");
                loggEvent(oppgave, egenskapFinner);
                //loggEvent(behandlingId, oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, bpeDto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, bpeDto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = nyOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, beslutterOppgave.getId());
                loggEvent(beslutterOppgave, egenskapFinner);
                //loggEvent(behandlingId, beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, bpeDto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, bpeDto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = nyOppgave(eksternId, bpeDto, behandling, prosesserFraAdmin);
                reserverOppgaveFraTidligereReservasjon(prosesserFraAdmin, reservasjon, papirsøknadOppgave.getId());
                loggEvent(behandlingId, papirsøknadOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, bpeDto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(papirsøknadOppgave, egenskapFinner);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpneOppgave = gjenåpneOppgaveVedEksternId(eksternId);
                log.info("Gjenåpner oppgave");
                loggEvent(behandlingId, gjenåpneOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, bpeDto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpneOppgave, egenskapFinner);
                break;
        }
    }

    private void loggEvent(Oppgave oppgave, OppgaveEgenskapFinner egenskapFinner) {
        List<AndreKriterierType> kriterier = egenskapFinner.getAndreKriterier();
        if (kriterier.contains(AndreKriterierType.TIL_BESLUTTER)) {
            loggEventType(oppgave, AndreKriterierType.TIL_BESLUTTER);
        } else if (kriterier.contains(AndreKriterierType.PAPIRSØKNAD)) {
            loggEventType(oppgave, AndreKriterierType.PAPIRSØKNAD);
        } else {
            loggEventType(oppgave, null);
        }
    }

    private void loggEventType(Oppgave oppgave, AndreKriterierType kriterie) {
        OppgaveEventLogg eventLogg = new OppgaveEventLogg(oppgave.getEksternId(),
                OppgaveEventType.OPPRETTET,
                kriterie,
                oppgave.getBehandlendeEnhet());
        getOppgaveRepository().lagre(eventLogg);
    }

    /**
     * @deprecated Bruk avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto, OppgaveEventType, LocalDateTime) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttFpsakOppgaveOgLoggEvent(UUID eksternId, FpsakBehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime frist){
        avsluttOppgaveForEksternId(eksternId);
        loggEvent(bpeDto.getBehandlingId(), eksternId, eventType, null, bpeDto.getBehandlendeEnhet(), frist);
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

    /**
     * @deprecated Bruk avsluttOppgaveHvisÅpen(UUID, List, String) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttOppgaveHvisÅpen(Long behandlingId, UUID eksternId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erSisteEventÅpningsevent()){
            if(eksternId != null) {
                loggEvent(behandlingId, eksternId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            }
            getOppgaveRepository().avsluttOppgaveForEksternId(eksternId);
        }
    }

    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String) og anvend eksternId
     */
    @Deprecated(since = "14.11.2019")
    private void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, behandlingId));

    }

    /**
     * @deprecated Bruk loggEvent(UUID, OppgaveEventType, AndreKriterierType, String, AksjonspunktDto) og anvend eksternId i stedet for behandlingId
     */
    @Deprecated(since = "14.11.2019")
    protected void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        getOppgaveRepository().lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist, behandlingId));
    }

    private Oppgave nyOppgave(UUID eksternId, BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak, boolean prosesserFraAdmin) {
        return opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem().name())
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

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist){
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }
}
