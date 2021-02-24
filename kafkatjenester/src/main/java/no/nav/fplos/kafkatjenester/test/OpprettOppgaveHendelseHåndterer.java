package no.nav.fplos.kafkatjenester.test;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.oppgavestatistikk.KøOppgaveHendelse;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.kafkatjenester.FpsakOppgaveEgenskapFinner;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHandler;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpprettOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OpprettOppgaveHendelseHåndterer.class);
    private final OppgaveRepository oppgaveRepository;
    private final OppgaveEgenskapHandler oppgaveEgenskapHandler;
    private final OppgaveStatistikk oppgaveStatistikk;
    private final BehandlingFpsak behandlingFpsak;

    public OpprettOppgaveHendelseHåndterer(OppgaveRepository oppgaveRepository,
                                           OppgaveEgenskapHandler oppgaveEgenskapHandler,
                                           OppgaveStatistikk oppgaveStatistikk,
                                           BehandlingFpsak behandlingFpsak) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHandler = oppgaveEgenskapHandler;
        this.oppgaveStatistikk = oppgaveStatistikk;
        this.behandlingFpsak = behandlingFpsak;
    }

    @Override
    public void håndter() {
        LOG.info("Oppretter oppgave");
        var oppgave = opprettOppgave();
        opprettOppgaveEventLogg(oppgave);
        opprettOppgaveEgenskaper(oppgave);
        oppgaveStatistikk.lagre(oppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
    }

    private Oppgave opprettOppgave() {
        var oppgave = oppgaveFra(behandlingFpsak);
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    private void opprettOppgaveEventLogg(Oppgave oppgave) {
        var oppgaveEventLogg = new OppgaveEventLogg(oppgave.getBehandlingId(),
                OppgaveEventType.OPPRETTET, null, oppgave.getBehandlendeEnhet());
        oppgaveRepository.lagre(oppgaveEventLogg);
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHandler.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private Oppgave oppgaveFra(BehandlingFpsak behandling) {
        return Oppgave.builder()
                .medSystem(Fagsystem.FPSAK.name())
                .medFagsakSaksnummer(Long.valueOf(behandling.getSaksnummer()))
                .medAktorId(Long.valueOf(behandling.getAktørId()))
                .medBehandlendeEnhet(behandling.getBehandlendeEnhetNavn())
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
