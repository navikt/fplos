package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import static no.nav.foreldrepenger.los.oppgave.AndreKriterierType.TIL_BESLUTTER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

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

    public void håndterBehandling(LosBehandlingDto behandlingDto) {
        var behandlingId = BehandlingId.fromUUID(behandlingDto.behandlingUuid());
        var oppgaveHistorikk = new OppgaveHistorikk(oppgaveRepository.hentOppgaveEventer(behandlingId));
        var aksjonspunkter = behandlingDto.aksjonspunkt();
        var egenskapFinner = new TilbakekrevingOppgaveEgenskapFinner(aksjonspunkter, behandlingDto.ansvarligSaksbehandlerIdent());
        var behandlendeEnhet = behandlingDto.behandlendeEnhetId();
        var event = eventFra(aksjonspunkter, oppgaveHistorikk, egenskapFinner, behandlendeEnhet);


        switch (event) {
            case LUKK_OPPGAVE_MANUELT_VENT -> {
                LOG.info("TBK Lukker oppgave, satt manuelt på vent.");
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.MANU_VENT, behandlendeEnhet);
            }
            case LUKK_OPPGAVE -> {
                LOG.info("TBK Lukker oppgave med behandlingId {}.", behandlingId);
                avsluttOppgaveForBehandling(behandlingId);
                loggEvent(behandlingId, OppgaveEventType.LUKKET, behandlendeEnhet);
            }
            case OPPRETT_OPPGAVE -> {
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave oppgave = opprettTilbakekrevingOppgave(behandlingId, behandlingDto);
                LOG.info("TBK Oppretter oppgave {} for behandlingId {}.", oppgave.getId(), behandlingId);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
                køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, behandlendeEnhet);
            }
            case OPPRETT_BESLUTTER_OPPGAVE -> {
                avsluttOppgaveHvisÅpen(behandlingId, oppgaveHistorikk, behandlendeEnhet);
                Oppgave beslutterOppgave = opprettTilbakekrevingOppgave(behandlingId, behandlingDto);
                LOG.info("TBK Oppretter beslutteroppgave.");
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(beslutterOppgave, egenskapFinner);
                køStatistikk.lagre(beslutterOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(behandlingId, OppgaveEventType.OPPRETTET, TIL_BESLUTTER, behandlendeEnhet);
            }
            case GJENÅPNE_OPPGAVE -> {
                var gjenåpnetOppgave = oppgaveTjeneste.gjenåpneTilbakekrevingOppgave(behandlingId);
                LOG.info("TBK Gjenåpner oppgave for behandlingId {}.", behandlingId);
                oppdaterOppgaveInformasjon(gjenåpnetOppgave, behandlingId, behandlingDto);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
                køStatistikk.lagre(gjenåpnetOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
                loggEvent(gjenåpnetOppgave.getBehandlingId(), OppgaveEventType.GJENAPNET, null, behandlendeEnhet);
            }
            case OPPDATER_ÅPEN_OPPGAVE -> {
                var oppdaterOppgave = oppgaveTjeneste.hentAktivTilbakekrevingOppgave(behandlingId).orElseThrow();
                LOG.info("TBK oppdaterer åpen tilbakekrevingOppgaveId {}", oppdaterOppgave.getId());
                oppdaterOppgaveInformasjon(oppdaterOppgave, behandlingId, behandlingDto);
                oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppdaterOppgave, egenskapFinner);
            }
            default -> throw new IllegalStateException(String.format("Ukjent event %s", event));
        }
    }

    private EventResultat eventFra(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter,
                                   OppgaveHistorikk oppgaveHistorikk,
                                   OppgaveEgenskapFinner egenskaper,
                                   String behandlendeEnhet) {
        var erTilBeslutter = egenskaper.getAndreKriterier().contains(TIL_BESLUTTER);

        if (aktivLosManuellVent(aksjonspunkter)) {
            return EventResultat.LUKK_OPPGAVE_MANUELT_VENT;
        }
        if (harAktiveLosAksjonspunkt(aksjonspunkter) && oppgaveHistorikk.erÅpenOppgave()) {
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
        if (harAktiveLosAksjonspunkt(aksjonspunkter)) {
            return EventResultat.OPPRETT_OPPGAVE;
        }
        return EventResultat.LUKK_OPPGAVE;
    }

    private static boolean harAktiveLosAksjonspunkt(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean aktivLosManuellVent(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream().anyMatch(a -> List.of("7001", "7002").contains(a.definisjon()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
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

    private void oppdaterOppgaveInformasjon(TilbakekrevingOppgave gjenåpnetOppgave, BehandlingId behandlingId, LosBehandlingDto bpeDto) {
        var tmp = oppgaveFra(behandlingId, bpeDto);
        gjenåpnetOppgave.avstemMed(tmp);
        oppgaveRepository.lagre(gjenåpnetOppgave);
    }

    private TilbakekrevingOppgave opprettTilbakekrevingOppgave(BehandlingId behandlingId, LosBehandlingDto hendelse) {
        return oppgaveRepository.opprettTilbakekrevingOppgave(oppgaveFra(behandlingId, hendelse));
    }

    private TilbakekrevingOppgave oppgaveFra(BehandlingId behandlingId, LosBehandlingDto hendelse) {
        return TilbakekrevingOppgave.tbuilder()
                .medBeløp(Optional.ofNullable(hendelse.tilbakeDto()).map(LosBehandlingDto.LosTilbakeDto::feilutbetaltBeløp).orElse(BigDecimal.ZERO))
                .medFeilutbetalingStart(feilutbetalingStart(hendelse))
                .medSystem(Fagsystem.FPTILBAKE.name())
                .medFagsakSaksnummer(Long.valueOf(hendelse.saksnummer()))
                .medAktorId(new AktørId(hendelse.aktørId().getAktørId()))
                .medBehandlendeEnhet(hendelse.behandlendeEnhetId())
                .medBehandlingType(OppgaveUtil.mapBehandlingstype(hendelse.behandlingstype()))
                .medBehandlingStatus(OppgaveUtil.mapBehandlingsstatus(hendelse.behandlingsstatus()))
                .medFagsakYtelseType(OppgaveUtil.mapYtelse(hendelse.ytelse()))
                .medAktiv(true)
                .medBehandlingOpprettet(hendelse.opprettetTidspunkt())
                .medUtfortFraAdmin(false)
                .medBehandlingId(behandlingId)
                .build();
    }

    private static LocalDateTime feilutbetalingStart(LosBehandlingDto hendelse) {
        return Optional.ofNullable(hendelse.tilbakeDto())
                .map(LosBehandlingDto.LosTilbakeDto::førsteFeilutbetalingDato)
                .map(LocalDate::atStartOfDay)
                .orElse(null);
    }

    private void avsluttOppgaveForBehandling(BehandlingId behandlingId) {
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLoggAvsluttTilknyttetReservasjon(behandlingId);
    }
}
