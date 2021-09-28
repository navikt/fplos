package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturFraBeslutterHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(ReturFraBeslutterHendelseHåndterer.class);

    private final OppgaveRepository oppgaveRepository;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public ReturFraBeslutterHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                              OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                              KøStatistikkTjeneste køStatistikk,
                                              BehandlingFpsak behandlingFpsak) {
        super(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak);
        this.oppgaveRepository = oppgaveRepository;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    // TODO: vurder å opprette automatisk reservasjon på saksbehandler 1

    @Override
    void håndterEksisterendeOppgave() {
        var behandlingId = behandlingFpsak.getBehandlingId();
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingId)
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.LUKKET)
                .build();
        oppgaveRepository.lagre(oel);
        LOG.info("Avslutter {} beslutteroppgave", SYSTEM);
    }

    @Override
    void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.builder()
                .behandlingId(oppgave.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.OPPRETTET)
                .build();
        oppgaveRepository.lagre(oel);
        LOG.info("Retur fra beslutter, oppretter {} saksbehandler-oppgave med oppgaveId {}", SYSTEM, oppgave.getId());
    }
}
