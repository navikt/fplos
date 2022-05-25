package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.TIL_BESLUTTER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ApplicationScoped
public class TilbakekrevingHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(TilbakekrevingHendelseHåndterer.class);
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveRepository oppgaveRepository;
    private KøStatistikkTjeneste køStatistikk;

    @Inject
    public TilbakekrevingHendelseHåndterer(OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveRepository oppgaveRepository,
                                           OppgaveTjeneste oppgaveTjeneste,
                                           KøStatistikkTjeneste køStatistikk) {
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveRepository = oppgaveRepository;
        this.køStatistikk = køStatistikk;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    TilbakekrevingHendelseHåndterer() {
        //CDI
    }

    public void håndter(TilbakekrevingHendelse hendelse) {
        var behandlingId = hendelse.getBehandlingId();
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        var aksjonspunkter = hendelse.getAksjonspunkter();
        var egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(aksjonspunkter, hendelse.getAnsvarligSaksbehandler());
        var behandlendeEnhet = hendelse.getBehandlendeEnhet();
        var event = eventFra(oppgaveHistorikk, egenskapFinner, aksjonspunkter, behandlendeEnhet);

        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT -> {
                LOG.info("TBK Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, behandlendeEnhet);
            }
            case LUKK_OPPGAVE -> {
                LOG.info("TBK Lukker oppgave med behandlingId {}.", behandlingId.toString());
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, behandlendeEnhet);
            }
            case OPPRETT_OPPGAVE -> {
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave oppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("TBK Oppretter oppgave {} for behandlingId {}.", oppgave.getId(), behandlingId);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, behandlendeEnhet);
            }
            case OPPRETT_BESLUTTER_OPPGAVE -> {
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(hendelse);
                LOG.info("TBK Oppretter beslutteroppgave.");
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                køStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, behandlendeEnhet);
            }
            case GJENÅPNE_OPPGAVE -> {
                var gjenåpnetOppgave = oppgaveTjeneste.gjenåpneTilbakekrevingOppgave(behandlingId);
                LOG.info("TBK Gjenåpner oppgave for behandlingId {}.", behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, hendelse);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                køStatistikk.lagre(gjenåpnetOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, behandlendeEnhet);
            }
            case OPPDATER_ÅPEN_OPPGAVE -> {
                var oppdaterOppgave = oppgaveTjeneste.hentAktivTilbakekrevingOppgave(behandlingId).orElseThrow();
                LOG.info("TBK oppdaterer åpen tilbakekrevingOppgaveId {}", oppdaterOppgave.getId());
                oppdaterOppgaveInformasjon(oppdaterOppgave, hendelse);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppdaterOppgave, egenskapFinner);
            }
            default -> throw new IllegalStateException(String.format("Ukjent event %s", event));
        }
    }

    private EventResultat eventFra(OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper,
                                   List<Aksjonspunkt> aksjonspunkter,
                                   String behandlendeEnhet) {
        var erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (aktivManuellVent(aksjonspunkter)) {
            return EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
        }
        if (harAktiveAksjonspunkt(aksjonspunkter) && oppgaveHistorikk.erÅpenOppgave()) {
            if (erTilBeslutter && !oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                return EventResultat.OPPRETT_BESLUTTER_OPPGAVE;
            }
            if (erTilBeslutter) {
                return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(behandlendeEnhet)
                        ? EventResultat.OPPDATER_ÅPEN_OPPGAVE
                        : EventResultat.OPPRETT_OPPGAVE;
            }
            if (oppgaveHistorikk.erSisteOpprettedeOppgaveTilBeslutter()) {
                // ikke til beslutter pt, dermed retur fra beslutter
                return EventResultat.OPPRETT_OPPGAVE;
            }
            return oppgaveHistorikk.erSisteOppgaveRegistrertPåEnhet(behandlendeEnhet)
                        ? EventResultat.OPPDATER_ÅPEN_OPPGAVE
                        : EventResultat.OPPRETT_OPPGAVE;
        }
        if (harAktiveAksjonspunkt(aksjonspunkter)) {
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
        var tmp = oppgaveFra(bpeDto);
        gjenåpnetOppgave.avstemMed(tmp);
        oppgaveRepository.lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(TilbakekrevingHendelse hendelse) {
        return oppgaveRepository.opprettTilbakekrevingOppgave(oppgaveFra(hendelse));
    }

    private TilbakekrevingOppgave oppgaveFra(TilbakekrevingHendelse hendelse) {
        return TilbakekrevingOppgave.tbuilder()
                .medBeløp(hendelse.getFeilutbetaltBeløp())
                .medFeilutbetalingStart(feilutbetalingStart(hendelse))
                .medHref(hendelse.getHref())
                .medSystem(hendelse.getFagsystem().name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.getSaksnummer()))
                .medAktorId(new AktørId(hendelse.getAktørId()))
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
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
    }
}
