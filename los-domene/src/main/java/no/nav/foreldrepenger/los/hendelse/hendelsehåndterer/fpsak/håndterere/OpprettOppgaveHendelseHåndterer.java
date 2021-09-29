package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;


public abstract class OpprettOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final KøStatistikkTjeneste køStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           KøStatistikkTjeneste køStatistikk,
                                           BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikk = køStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    public void håndter() {
        håndterEksisterendeOppgave();
        var oppgave = opprettOppgave();
        opprettOppgaveEgenskaper(oppgave);
        opprettOppgaveEventLogg(oppgave);
        køStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    abstract void opprettOppgaveEventLogg(Oppgave oppgave);

    abstract void håndterEksisterendeOppgave();

    private Oppgave opprettOppgave() {
        var oppgave = oppgave(behandlingFpsak);
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

}
