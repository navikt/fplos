package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class GenerellOpprettOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(GenerellOpprettOppgaveOppgavetransisjonHåndterer.class);

    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikk;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private ReservasjonTjeneste reservasjonTjeneste;

    @Inject
    public GenerellOpprettOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                            OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                            KøStatistikkTjeneste køStatistikk,
                                                            ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikk = køStatistikk;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }

    public GenerellOpprettOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        håndterEksisterendeOppgave(behandlingId);
        var oppgave = opprettOppgave(behandlingId, behandling);
        opprettOppgaveEgenskaper(oppgave, behandling);
        reserverDersomAnsvarligSatt(behandling, eventHistorikk, oppgave);
        opprettOppgaveEventLogg(oppgave);
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPRETT_OPPGAVE;
    }

    private Oppgave opprettOppgave(BehandlingId behandlingId, LosBehandlingDto behandling) {
        var oppgave = OppgaveUtil.oppgave(behandlingId, behandling);
        oppgaveTjeneste.lagre(oppgave);
        return oppgave;
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, LosBehandlingDto behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    void håndterEksisterendeOppgave(BehandlingId behandlingId) {
        oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId).filter(Oppgave::getAktiv).ifPresent(o -> {
            throw new IllegalStateException(String.format("Finnes aktiv oppgave (oppgaveId %s) fra før, gir opp håndtering av hendelse", o.getId()));
        });
    }

    void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.opprettetOppgaveEvent(oppgave);
        oppgaveTjeneste.lagre(oel);
        LOG.info("Oppretter {}-oppgave med id {}", SYSTEM, oppgave.getId());
    }

    private void reserverDersomAnsvarligSatt(LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk, Oppgave oppgave) {
        if (behandling.ansvarligSaksbehandlerIdent() != null
            && (eventHistorikk.erUtenHistorikk() || eventHistorikk.erPåVent() || erManuellRevurdering(behandling))) {
            reservasjonTjeneste.reserverOppgave(oppgave, behandling.ansvarligSaksbehandlerIdent());
        }
    }

    private boolean erManuellRevurdering(LosBehandlingDto losBehandlingDto) {
        return Behandlingstype.REVURDERING.equals(losBehandlingDto.behandlingstype())
            && losBehandlingDto.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.MANUELL::equals);
    }
}
