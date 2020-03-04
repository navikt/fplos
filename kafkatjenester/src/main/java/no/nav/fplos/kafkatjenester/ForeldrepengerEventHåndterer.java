package no.nav.fplos.kafkatjenester;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
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
import no.nav.fplos.kafkatjenester.eventresultat.ForeldrepengerEventMapper;
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
        var behandlingId = new BehandlingId(dto.getEksternId());
        BehandlingFpsak behandling = foreldrePengerBehandlingRestKlient.getBehandling(behandlingId);
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));

        List<Aksjonspunkt> aksjonspunkt = Optional.ofNullable(behandling.getAksjonspunkter()).orElse(Collections.emptyList());

        OppgaveEgenskapFinner egenskapFinner = new FpsakOppgaveEgenskapFinner(behandling, aksjonspunkt);
        EventResultat eventResultat = ForeldrepengerEventMapper.signifikantEventFra(aksjonspunkt, oppgaveHistorikk, dto.getBehandlendeEnhet());

        switch (eventResultat) {
            case FERDIG:
                LOG.info("Ikke relevant for oppgaver");
                break;
            case LUKK_OPPGAVE:
                LOG.info("Lukker oppgave");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, dto, OppgaveEventType.LUKKET, null);
                break;
            case LUKK_OPPGAVE_VENT:
                LOG.info("Behandling satt automatisk på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, dto, OppgaveEventType.VENT, finnVentAksjonspunktFrist(aksjonspunkt));
                break;
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Behandling satt manuelt på vent, lukker oppgave.");
                avsluttFpsakOppgaveOgLoggEvent(behandlingId, dto, OppgaveEventType.MANU_VENT, finnManuellAksjonspunktFrist(aksjonspunkt));
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave oppgave = nyOppgave(behandlingId, dto, behandling);
                LOG.info("Oppretter oppgave");
                loggEvent(oppgave, egenskapFinner);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = nyOppgave(behandlingId, dto, behandling);
                loggEvent(beslutterOppgave, egenskapFinner);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                break;
            case OPPRETT_PAPIRSØKNAD_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave papirsøknadOppgave = nyOppgave(behandlingId, dto, behandling);
                loggEvent(papirsøknadOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, AndreKriterierType.PAPIRSØKNAD, dto.getBehandlendeEnhet());
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(papirsøknadOppgave, egenskapFinner);
                break;
            case GJENÅPNE_OPPGAVE:
                Oppgave gjenåpneOppgave = gjenåpneOppgave(behandlingId);
                LOG.info("Gjenåpner oppgave");
                loggEvent(gjenåpneOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, dto.getBehandlendeEnhet());
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
        OppgaveEventLogg eventLogg = new OppgaveEventLogg(oppgave.getBehandlingId(),
                OppgaveEventType.OPPRETTET,
                kriterie,
                oppgave.getBehandlendeEnhet());
        oppgaveRepository.lagre(eventLogg);
    }

    private void avsluttFpsakOppgaveOgLoggEvent(BehandlingId behandlingId, FpsakBehandlingProsessEventDto dto, OppgaveEventType eventType, LocalDateTime frist) {
        avsluttOppgaveForBehandling(behandlingId);
        loggEvent(behandlingId, eventType, null, dto.getBehandlendeEnhet(), frist);
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

    private void avsluttOppgaveHvisÅpen(BehandlingId behandlingId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erSisteEventÅpningsevent()){
            if (behandlingId != null) {
                loggEvent(behandlingId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            }
            oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        }
    }

    private void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist));
    }

    private Oppgave nyOppgave(BehandlingId behandlingId, BehandlingProsessEventDto dto, BehandlingFpsak fraFpsak) {
        return oppgaveRepository.opprettOppgave(Oppgave.builder()
                .medSystem(dto.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(dto.getSaksnummer()))
                .medAktorId(Long.valueOf(dto.getAktørId()))
                .medBehandlendeEnhet(dto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(dto.getOpprettetBehandling())
                .medUtfortFraAdmin(false)
                .medBehandlingStatus(BehandlingStatus.fraKode(fraFpsak.getStatus()))
                .medBehandlingId(behandlingId)
                .build());
    }

    private Oppgave gjenåpneOppgave(BehandlingId behandlingId) {
        return oppgaveRepository.gjenåpneOppgaveForBehandling(behandlingId);
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
