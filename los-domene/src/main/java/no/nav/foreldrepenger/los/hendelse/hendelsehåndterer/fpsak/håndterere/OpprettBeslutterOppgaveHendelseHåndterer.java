package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpprettBeslutterOppgaveHendelseHåndterer extends OpprettOppgaveHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(OpprettBeslutterOppgaveHendelseHåndterer.class);
    private final OppgaveRepository oppgaveRepository;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;


    public OpprettBeslutterOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                                    OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                    KøStatistikkTjeneste køStatistikk,
                                                    BehandlingFpsak behandlingFpsak) {
        super(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak);
        this.oppgaveRepository = oppgaveRepository;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    void håndterEksisterendeOppgave() {
        // TODO: av og til er saksbehandlers oppgave allerede lukket. Vurder en sjekk på dette før man logger i OEL osv
        var behandlingId = behandlingFpsak.getBehandlingId();
        køStatistikk.lagre(behandlingId, KøOppgaveHendelse.LUKKET_OPPGAVE);
        oppgaveRepository.avsluttOppgaveForBehandling(behandlingId);
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingId)
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.LUKKET)
                .build();
        oppgaveRepository.lagre(oel);
        LOG.info("Avslutter saksbehandler1 oppgave");
    }

    @Override
    void opprettOppgaveEventLogg(Oppgave oppgave) {
        LOG.info("Oppretter {} oppgave til beslutter", FpsakHendelseHåndterer.SYSTEM);
        var oel = OppgaveEventLogg.builder()
                .type(OppgaveEventType.OPPRETTET)
                .behandlingId(behandlingFpsak.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .andreKriterierType(AndreKriterierType.TIL_BESLUTTER)
                .build();
        oppgaveRepository.lagre(oel);
    }

}
