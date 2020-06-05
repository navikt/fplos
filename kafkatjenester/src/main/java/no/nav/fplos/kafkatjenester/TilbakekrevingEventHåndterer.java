package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.TilbakekrevingEventMapper;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.TIL_BESLUTTER;

@ApplicationScoped
public class TilbakekrevingEventHåndterer implements EventHåndterer<TilbakebetalingBehandlingProsessEventDto> {

    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHåndterer.class);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;
    private OppgaveRepository oppgaveRepository;

    @Inject
    public TilbakekrevingEventHåndterer(OppgaveRepository oppgaveRepository, OppgaveEgenskapHandler oppgaveEgenskapHandler) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
    }

    TilbakekrevingEventHåndterer() {
        //CDI
    }

    @Override
    public void håndterEvent(TilbakebetalingBehandlingProsessEventDto dto) {
        var behandlingId = new BehandlingId(dto.getEksternId());
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        OppgaveEgenskapFinner egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(dto.getAksjonspunktKoderMedStatusListe(),
                dto.getAnsvarligSaksbehandlerIdent());
        EventResultat event = eventFra(dto, oppgaveHistorikk, egenskapFinner);

        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT:
                log.info("Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, dto.getBehandlendeEnhet());
                break;
            case LUKK_OPPGAVE:
                log.info("Lukker oppgave med behandlingId {}.", behandlingId.toString());
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, dto.getBehandlendeEnhet());
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave oppgave = opprettTilbakekrevingOppgave(dto);
                log.info("Oppretter tilbakekrevingsoppgave {} for behandlingId {}.", oppgave.getId(), behandlingId);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                loggEvent(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, dto.getBehandlendeEnhet());
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, dto.getBehandlendeEnhet());
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(dto);
                log.info("Oppretter beslutteroppgave.");
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, dto.getBehandlendeEnhet());
                break;
            case GJENÅPNE_OPPGAVE:
                TilbakekrevingOppgave gjenåpnetOppgave = oppgaveRepository.gjenåpneTilbakekrevingOppgave(behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, dto);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                log.info("Gjenåpner oppgave for behandlingId {}.", behandlingId);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, dto.getBehandlendeEnhet());
                break;
        }
    }

    private EventResultat eventFra(TilbakebetalingBehandlingProsessEventDto bpeDto, OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper) {
        EventResultat event = TilbakekrevingEventMapper.tilbakekrevingEventFra(bpeDto);
        OppgaveEventLogg sisteOppgaveEvent = oppgaveHistorikk.getSisteÅpningsEvent();
        boolean erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (event == EventResultat.OPPRETT_OPPGAVE && sisteOppgaveEvent != null && sisteOppgaveEvent.getEventType().erÅpningsevent()) {
            if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() != TIL_BESLUTTER) {
                return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
            } else if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.GJENÅPNE_OPPGAVE;
            } else if (!erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.OPPRETT_OPPGAVE;
            } else if (sisteOppgaveEvent.getBehandlendeEnhet().equals(bpeDto.getBehandlendeEnhet())) {
                return EventResultat.GJENÅPNE_OPPGAVE;
            }
        }
        return event;
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, null, behandlendeEnhet));
    }


    private void avsluttOppgaveHvisÅpen(BehandlingId behandlingId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erSisteEventÅpningsevent()) {
            loggEvent(behandlingId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        }
    }

    private void oppdaterOppgaveInformasjon(TilbakekrevingOppgave gjenåpnetOppgave, TilbakebetalingBehandlingProsessEventDto bpeDto) {
        TilbakekrevingOppgave tmp = oppgaveFra(bpeDto);
        gjenåpnetOppgave.avstemMed(tmp);
        oppgaveRepository.lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakebetalingBehandlingProsessEventDto dto) {
        return oppgaveRepository.opprettTilbakekrevingEgenskaper(oppgaveFra(dto));
    }

    private TilbakekrevingOppgave oppgaveFra(TilbakebetalingBehandlingProsessEventDto dto) {
        return TilbakekrevingOppgave.tbuilder()
                .medBelop(dto.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart(dto))
                .medHref(dto.getHref())
                .medSystem(dto.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(dto.getSaksnummer()))
                .medAktorId(Long.valueOf(dto.getAktørId()))
                .medBehandlendeEnhet(dto.getBehandlendeEnhet())
                .medBehandlingType(BehandlingType.fraKode(dto.getBehandlingTypeKode()))
                .medFagsakYtelseType(FagsakYtelseType.fraKode(dto.getYtelseTypeKode()))
                .medAktiv(true)
                .medBehandlingOpprettet(dto.getOpprettetBehandling())
                .medUtfortFraAdmin(false)
                .medBehandlingId(new BehandlingId(dto.getEksternId()))
                .build();
    }

    private static LocalDateTime feilutbetalingStart(TilbakebetalingBehandlingProsessEventDto dto) {
        return Optional.ofNullable(dto.getFørsteFeilutbetaling())
                .map(LocalDate::atStartOfDay)
                .orElse(null);
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
