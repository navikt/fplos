package no.nav.fplos.kafkatjenester;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.TIL_BESLUTTER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
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

@ApplicationScoped
public class TilbakekrevingHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingHendelseHåndterer.class);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler;
    private OppgaveRepository oppgaveRepository;
    private OppgaveStatistikk oppgaveStatistikk;

    @Inject
    public TilbakekrevingHendelseHåndterer(OppgaveEgenskapHandler oppgaveEgenskapHandler,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveStatistikk oppgaveStatistikk) {
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveStatistikk = oppgaveStatistikk;
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
                LOG.info("TBK Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, behandlendeEnhet);
                break;
            case LUKK_OPPGAVE:
                LOG.info("TBK Lukker oppgave med behandlingId {}.", behandlingId.toString());
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, behandlendeEnhet);
                break;
            case OPPRETT_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave oppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("TBK Oppretter oppgave {} for behandlingId {}.", oppgave.getId(), behandlingId);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                oppgaveStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, behandlendeEnhet);
                break;
            case OPPRETT_BESLUTTER_OPPGAVE:
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("TBK Oppretter beslutteroppgave.");
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                oppgaveStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, behandlendeEnhet);
                break;
            case GJENÅPNE_OPPGAVE:
                TilbakekrevingOppgave gjenåpnetOppgave = oppgaveRepository.gjenåpneTilbakekrevingOppgave(behandlingId);
                LOG.info("TBK Gjenåpner oppgave for behandlingId {}.", behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, hendelse);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                oppgaveStatistikk.lagre(gjenåpnetOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, behandlendeEnhet);
                break;
            case OPPDATER_ÅPEN_OPPGAVE:
                var oppdaterOppgave = oppgaveRepository.hentAktivTilbakekrevingOppgave(behandlingId).orElseThrow();
                LOG.info("TBK oppdaterer åpen tilbakekrevingOppgaveId {}", oppdaterOppgave.getId());
                oppdaterOppgaveInformasjon(oppdaterOppgave, hendelse);
                oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppdaterOppgave, egenskapFinner);
                break;
            default:
                throw new IllegalStateException(String.format("Ukjent event %s", event));
        }
    }

    private EventResultat eventFra(OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper,
                                   List<Aksjonspunkt> aksjonspunkter,
                                   String behandlendeEnhet) {
        boolean erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (aktivManuellVent(aksjonspunkter)) {
            return EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
        } else if (harAktiveAksjonspunkt(aksjonspunkter) && oppgaveHistorikk.erÅpenOppgave()) {
            if (erTilBeslutter && !oppgaveHistorikk.erSisteOppgaveTilBeslutter()) {
                return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
            } else if (erTilBeslutter) {
                return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(behandlendeEnhet)
                        ? EventResultat.OPPDATER_ÅPEN_OPPGAVE
                        : EventResultat.OPPRETT_OPPGAVE;
            } else if (oppgaveHistorikk.erSisteOppgaveTilBeslutter()) {
                // ikke til beslutter pt, dermed retur fra beslutter
                return EventResultat.OPPRETT_OPPGAVE;
            } else {
                return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(behandlendeEnhet)
                        ? EventResultat.OPPDATER_ÅPEN_OPPGAVE
                        : EventResultat.OPPRETT_OPPGAVE;
            }
        } else if (harAktiveAksjonspunkt(aksjonspunkter)) {
            return EventResultat.OPPRETT_OPPGAVE;
        }
        return EventResultat.LUKK_OPPGAVE;
    }

    private static boolean harAktiveAksjonspunkt(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(Aksjonspunkt::erOpprettet);
    }

    private static boolean aktivManuellVent(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> List.of("7001", "7002").contains(a.getKode()) && a.erOpprettet());
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected void loggEvent(BehandlingId behandlingId, OppgaveEventType oppgaveEventType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(behandlingId, oppgaveEventType, null, behandlendeEnhet));
    }


    private void avsluttOppgaveHvisÅpen(BehandlingId behandlingId, OppgaveHistorikk oppgaveHistorikk, String behandlendeEnhet) {
        if (oppgaveHistorikk.erÅpenOppgave()) {
            loggEvent(behandlingId, OppgaveEventType.LUKKET, null, behandlendeEnhet);
            avsluttOppgaveForBehandling(behandlingId);
        }
    }

    private void oppdaterOppgaveInformasjon(TilbakekrevingOppgave gjenåpnetOppgave, TilbakekrevingHendelse bpeDto) {
        TilbakekrevingOppgave tmp = oppgaveFra(bpeDto);
        gjenåpnetOppgave.avstemMed(tmp);
        oppgaveRepository.lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingHendelse hendelse) {
        return oppgaveRepository.opprettTilbakekrevingOppgave(oppgaveFra(hendelse));
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
        oppgaveStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
    }
}
