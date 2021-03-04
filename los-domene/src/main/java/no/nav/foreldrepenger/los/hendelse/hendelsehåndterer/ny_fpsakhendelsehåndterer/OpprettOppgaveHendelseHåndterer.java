package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperTjeneste;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpprettOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OpprettOppgaveHendelseHåndterer.class);
    private final String system = Fagsystem.FPSAK.name();
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final OppgaveStatistikk oppgaveStatistikk;
    private final AktuelleOppgaveEgenskaperTjeneste aktuelleOppgaveEgenskapTjeneste;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           AktuelleOppgaveEgenskaperTjeneste aktuelleOppgaveEgenskapTjeneste,
                                           BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.aktuelleOppgaveEgenskapTjeneste = aktuelleOppgaveEgenskapTjeneste;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    public void håndter() {
        var oppgave = opprettOppgave();
        LOG.info("Oppretter {}-oppgave med id {}", system, oppgave.getId());
        opprettOppgaveEgenskaper(oppgave);
        opprettOppgaveEventLogg(oppgave);
        oppgaveStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    private Oppgave opprettOppgave() {
        var oppgave = oppgaveFra(behandlingFpsak);
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oel = OppgaveEventLogg.opprettetOppgaveEvent(oppgave);
        oppgaveRepository.lagre(oel);
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave) {
        var nyeEgenskaper = aktuelleOppgaveEgenskapTjeneste.egenskaperForFpsak(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, nyeEgenskaper);

        //var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        //oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private Oppgave oppgaveFra(BehandlingFpsak behandling) {
        return Oppgave.builder()
                .medSystem(system)
                .medFagsakSaksnummer(Long.valueOf(behandling.getSaksnummer()))
                .medAktorId(Long.valueOf(behandling.getAktørId()))
                .medBehandlendeEnhet(behandling.getBehandlendeEnhetId())
                .medBehandlingType(behandling.getBehandlingType())
                .medFagsakYtelseType(behandling.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(behandling.getBehandlingOpprettet())
                .medUtfortFraAdmin(false)
                .medBehandlingStatus(BehandlingStatus.fraKode(behandling.getStatus()))
                .medBehandlingId(behandling.getBehandlingId())
                .medForsteStonadsdag(behandling.getFørsteUttaksdag())
                .medBehandlingsfrist(behandling.getBehandlingstidFrist())
                .build();
    }

}
