package no.nav.fplos.kafkatjenester;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.TIL_BESLUTTER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.loslager.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.eventresultat.EventResultat;
import no.nav.fplos.kafkatjenester.eventresultat.TilbakekrevingEventMapper;

@ApplicationScoped
public class TilbakekrevingHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingHendelseHåndterer.class);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;
    private OppgaveRepository oppgaveRepository;

    @Inject
    public TilbakekrevingHendelseHåndterer(OppgaveEgenskapHandler oppgaveEgenskapHandler,
                                           OppgaveRepository oppgaveRepository) {
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
        this.oppgaveRepository = oppgaveRepository;
    }

    TilbakekrevingHendelseHåndterer() {
        //CDI
    }

    public void håndter(TilbakekrevingHendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        OppgaveHistorikk oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        var aksjonspunkter = hendelse.getAksjonspunkter();
        OppgaveEgenskapFinner egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(aksjonspunkter, hendelse.getAnsvarligSaksbehandler());
        var behandlendeEnhet = hendelse.getBehandlendeEnhet();
        EventResultat event = eventFra(oppgaveHistorikk, egenskapFinner, aksjonspunkter, behandlendeEnhet);

        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT:
                LOG.info("Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, behandlendeEnhet);
                break;
            case LUKK_OPPGAVE:
                LOG.info("Lukker oppgave med behandlingId {}.", behandlingId.toString());
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, behandlendeEnhet);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave oppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("Oppretter tilbakekrevingsoppgave {} for behandlingId {}.", oppgave.getId(), behandlingId);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                loggEvent(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, behandlendeEnhet);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("Oppretter beslutteroppgave.");
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, behandlendeEnhet);
                break;
            case GJENÅPNE_OPPGAVE:
                TilbakekrevingOppgave gjenåpnetOppgave = oppgaveRepository.gjenåpneTilbakekrevingOppgave(behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, hendelse);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                LOG.info("Gjenåpner oppgave for behandlingId {}.", behandlingId);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, behandlendeEnhet);
                break;
        }
    }

    private EventResultat eventFra(OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper,
                                   List<Aksjonspunkt> aksjonspunkter,
                                   String behandlendeEnhet) {
        EventResultat event = TilbakekrevingEventMapper.tilbakekrevingEventFra(aksjonspunkter);
        OppgaveEventLogg sisteOppgaveEvent = oppgaveHistorikk.getSisteÅpningsEvent();
        boolean erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (event == EventResultat.OPPRETT_OPPGAVE && sisteOppgaveEvent != null && sisteOppgaveEvent.getEventType().erÅpningsevent()) {
            if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() != TIL_BESLUTTER) {
                return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
            } else if (erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.GJENÅPNE_OPPGAVE;
            } else if (!erTilBeslutter && sisteOppgaveEvent.getAndreKriterierType() == TIL_BESLUTTER) {
                return EventResultat.OPPRETT_OPPGAVE;
            } else if (sisteOppgaveEvent.getBehandlendeEnhet().equals(behandlendeEnhet)) {
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

    private void oppdaterOppgaveInformasjon(TilbakekrevingOppgave gjenåpnetOppgave, TilbakekrevingHendelse bpeDto) {
        TilbakekrevingOppgave tmp = oppgaveFra(bpeDto);
        gjenåpnetOppgave.avstemMed(tmp);
        oppgaveRepository.lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingHendelse hendelse) {
        return oppgaveRepository.opprettTilbakekrevingEgenskaper(oppgaveFra(hendelse));
    }

    private TilbakekrevingOppgave oppgaveFra(TilbakekrevingHendelse hendelse) {
        return TilbakekrevingOppgave.tbuilder()
                .medBelop(hendelse.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart(hendelse))
                .medHref(hendelse.getHref())
                .medSystem(hendelse.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.getSaksnummer()))
                .medAktorId(Long.valueOf(hendelse.getAktørId()))
                .medBehandlendeEnhet(hendelse.getBehandlendeEnhet())
                .medBehandlingType(hendelse.getBehandlingType())
                .medFagsakYtelseType(hendelse.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(hendelse.getBehandlingOpprettetTidspunkt())
                .medUtfortFraAdmin(false)
                .medBehandlingId(hendelse.getBehandlingId())
                .build();
    }

    private static LocalDateTime feilutbetalingStart(TilbakekrevingHendelse hendelse) {
        return Optional.ofNullable(hendelse.getFørsteFeilutbetalingDato())
                .map(LocalDate::atStartOfDay)
                .orElse(null);
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
