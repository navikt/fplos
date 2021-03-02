package no.nav.fplos.kafkatjenester.fpsakhendelsehåndterer;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.kafkatjenester.FpsakOppgaveEgenskapFinner;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHåndterer;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpprettOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OpprettOppgaveHendelseHåndterer.class);
    private final String system = Fagsystem.FPSAK.name();
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private final OppgaveStatistikk oppgaveStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveStatistikk = oppgaveStatistikk;
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
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
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
