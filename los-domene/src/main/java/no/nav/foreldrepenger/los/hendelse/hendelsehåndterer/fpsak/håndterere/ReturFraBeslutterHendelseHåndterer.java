package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturFraBeslutterHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterHendelseHåndterer.class);
    private static final boolean IS_PROD = Environment.current().isProd();

    private final OppgaveTjeneste oppgaveTjeneste;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;
    private final ReservasjonTjeneste reservasjonTjeneste;

    public ReturFraBeslutterHendelseHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                              OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                              ReservasjonTjeneste reservasjonTjeneste,
                                              KøStatistikkTjeneste køStatistikk,
                                              BehandlingFpsak behandlingFpsak) {
        super(oppgaveTjeneste, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak);
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    // TODO: vurder å opprette automatisk reservasjon på saksbehandler 1

    @Override
    void håndterEksisterendeOppgave() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveTjeneste.avsluttOppgaveUtenEventLogg(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingId)
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.LUKKET)
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }

    @Override
    Oppgave opprettOppgave() {
        var oppgave = super.opprettOppgave();
        if (!IS_PROD) {
            reservasjonTjeneste.opprettReservasjon(oppgave, behandlingFpsak.getAnsvarligSaksbehandler(), "Retur fra beslutter");
            LOG.info("Retur fra beslutter, oppretter oppgave og flytter reservasjon til ansvarlig saksbehandler");
        }
        return oppgave;
    }

    @Override
    void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.builder()
                .behandlingId(oppgave.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.OPPRETTET)
                .build();
        oppgaveTjeneste.lagre(oel);
        LOG.info("Retur fra beslutter, oppretter {} saksbehandler-oppgave med oppgaveId {}", SYSTEM, oppgave.getId());
    }
}
