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

        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        if (eksternId == null) {
            eksternId = behandling.getUuid();
        }

        List<OppgaveEventLogg> tidligereEventer = oppgaveRepository.hentEventerForEksternId(eksternId);
        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter())
                .orElse(Collections.emptyList());

        OppgaveEgenskapFinner egenskapFinner = new OppgaveEgenskapFinner(behandling, tidligereEventer, aksjonspunkt);

        EventResultat eventResultat = FpsakEventMapper.signifikantEventFra(aksjonspunkt, tidligereEventer, dto.getBehandlendeEnhet());

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
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, tidligereEventer, dto.getBehandlendeEnhet());
                Oppgave oppgave = nyOppgave(eksternId, dto, behandling);
                LOG.info("Oppretter oppgave");
                loggEvent(behandlingId, oppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, tidligereEventer, dto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = nyOppgave(eksternId, dto, behandling);
                loggEvent(behandlingId, beslutterOppgave.getEksternId(), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, eksternId, tidligereEventer, dto.getBehandlendeEnhet());
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

    /**
     * @deprecated Bruk avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto, OppgaveEventType, LocalDateTime) i stedet
     */
    @Deprecated(since = "14.11.2019")
    private void avsluttFpsakOppgaveOgLoggEvent(UUID eksternId, FpsakBehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime frist) {
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
    private void avsluttOppgaveHvisÅpen(Long behandlingId, UUID eksternId, List<OppgaveEventLogg> oppgaveEventLogger, String behandlendeEnhet) {
        if (!oppgaveEventLogger.isEmpty() && oppgaveEventLogger.get(0).getEventType().erÅpningsevent()) {
            if (eksternId != null) {
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

    private Oppgave nyOppgave(UUID eksternId, BehandlingProsessEventDto bpeDto, BehandlingFpsak fraFpsak) {
        return oppgaveRepository.opprettOppgave(Oppgave.builder()
                .medSystem(bpeDto.getFagsystem().name())
                .medBehandlingId(bpeDto.getBehandlingId())
                .medFagsakSaksnummer(Long.valueOf(bpeDto.getSaksnummer()))
                .medAktorId(Long.valueOf(bpeDto.getAktørId()))
                .medBehandlendeEnhet(bpeDto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(bpeDto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(bpeDto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(bpeDto.getOpprettetBehandling())
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
        return oppgaveRepository.gjenåpneOppgaveForEksternId(eksternId);
    }

    private void avsluttOppgaveForEksternId(UUID externId) {
        oppgaveRepository.avsluttOppgaveForEksternId(externId);
    }
}
