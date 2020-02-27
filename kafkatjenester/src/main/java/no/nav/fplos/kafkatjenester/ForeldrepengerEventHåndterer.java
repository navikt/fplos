package no.nav.fplos.kafkatjenester;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.FpsakEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

@ApplicationScoped
public class ForeldrepengerEventHåndterer implements EventHåndterer<FpsakBehandlingProsessEventDto> {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerEventHåndterer.class);

    private ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;

    @Inject
    public ForeldrepengerEventHåndterer(OppgaveRepository oppgaveRepository,
                                        ForeldrepengerBehandlingRestKlient foreldrePengerBehandlingRestKlient,
                                        OppgaveEgenskapHandler oppgaveEgenskapHandler) {
        this.foreldrePengerBehandlingRestKlient = foreldrePengerBehandlingRestKlient;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
    }

    ForeldrepengerEventHåndterer() {
        //CDI
    }

    @Override
    public void håndterEvent(FpsakBehandlingProsessEventDto dto) {
        Long behandlingId = dto.getBehandlingId();
        UUID eksternId = dto.getEksternId();
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(eksternId));

        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        if (eksternId == null) {
            eksternId = behandling.getUuid();
        }

        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        OppgaveEgenskapFinner egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling, aksjonspunkt);

        EventResultat eventResultat = FpsakEventMapper.signifikantEventFra(aksjonspunkt, oppgaveHistorikk, dto.getBehandlendeEnhet());

        switch (eventResultat) {
            case LUKK_OPPGAVE:
                LOG.info("Lukker oppgave");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, dto, OppgaveEventType.LUKKET, null);
                break;
            case LUKK_OPPGAVE_VENT:
                LOG.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, dto, OppgaveEventType.VENT, finnVentAksjonspunktFrist(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(eksternId, dto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave oppgave = nyOppgave(eksternId, dto, behandling);
                LOG.info("Oppretter oppgave");
                loggEvent(oppgave, egenskapFinner);
                //loggEvent(behandlingId, oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = nyOppgave(eksternId, dto, behandling);
                loggEvent(beslutterOppgave, egenskapFinner);
                //loggEvent(behandlingId, beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = nyOppgave(eksternId, dto, behandling);
                loggEvent(behandlingId, papirsøknadOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(papirsøknadOppgave, egenskapFinner);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpneOppgave = gjenåpneOppgaveVedEksternId(eksternId);
                LOG.info("Gjenåpner oppgave");
                loggEvent(behandlingId, gjenåpneOppgave.getEksternId(), OppgaveEventType.GJENAPNET, null, dto.getBehandlendeEnhet());
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
        oppgaveRepository.lagre(eventLogg);
    }

    /**
     * @deprecated Bruk avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto, OppgaveEventType, LocalDateTime) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttFpsakOppgaveOgLoggEvent(UUID eksternId, FpsakBehandlingProsessEventDto dto, OppgaveEventType eventType, LocalDateTime frist) {
        avsluttOppgaveForEksternId(eksternId);
        loggEvent(dto.getBehandlingId(), eksternId, eventType, null, dto.getBehandlendeEnhet(), frist);
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
            oppgaveRepository.avsluttOppgaveForEksternId(eksternId);
        }
    }

    /**
     * @deprecated Bruk loggEvent(Long, OppgaveEventType, AndreKriterierType, String) og anvend eksternId
     */
    @Deprecated(since = "14.11.2019")
    private void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, behandlingId));
    }

    /**
     * @deprecated Bruk loggEvent(UUID, OppgaveEventType, AndreKriterierType, String, AksjonspunktDto) og anvend eksternId i stedet for behandlingId
     */
    @Deprecated(since = "14.11.2019")
    protected void loggEvent(Long behandlingId, UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist, behandlingId));
    }

    private Oppgave nyOppgave(UUID eksternId, BehandlingProsessEventDto dto, BehandlingFpsak fraFpsak) {
        return oppgaveRepository.opprettOppgave(Oppgave.builder()
                .medSystem(dto.getFagsystem().name())
                .medBehandlingId(dto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(dto.getSaksnummer()))
                .medAktorId(Long.valueOf(dto.getAktørId()))
                .medBehandlendeEnhet(dto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(dto.getOpprettetBehandling())
                .medForsteStonadsdag(fraFpsak.getFørsteUttaksdag())
                .medUtfortFraAdmin(false)
                .medBehandlingsfrist(hentBehandlingstidFrist(fraFpsak.getBehandlingstidFrist()))
                .medBehandlingStatus(BehandlingStatus.fraKode(fraFpsak.getStatus()))
                .medEksternId(eksternId)
                .build());
    }

    private static LocalDateTime hentBehandlingstidFrist(LocalDate behandlingstidFrist) {
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }

    private Oppgave gjenåpneOppgaveVedEksternId(UUID eksternId) {
        return oppgaveRepository.gjenåpneOppgave(eksternId);
    }

    private void avsluttOppgaveForEksternId(UUID externId) {
        oppgaveRepository.avsluttOppgaveForEksternId(externId);
    }
}
