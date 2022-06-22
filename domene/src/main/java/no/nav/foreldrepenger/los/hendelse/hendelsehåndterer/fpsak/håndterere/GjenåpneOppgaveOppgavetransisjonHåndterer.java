package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GjenåpneOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(GjenåpneOppgaveOppgavetransisjonHåndterer.class);

    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private KøStatistikkTjeneste køStatistikk;
    private ReservasjonTjeneste reservasjonTjeneste;

    @Inject
    public GjenåpneOppgaveOppgavetransisjonHåndterer(OppgaveRepository oppgaveRepository,
                                                     OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                     KøStatistikkTjeneste køStatistikk,
                                                     ReservasjonTjeneste reservasjonTjeneste) {
        this.oppgaveRepository = oppgaveRepository;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.køStatistikk = køStatistikk;
        this.reservasjonTjeneste = reservasjonTjeneste;
    }

    public GjenåpneOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingFpsak behandlingFpsak) {
        var behandlingId = behandlingFpsak.getBehandlingId();
        var oppgaveHistorikk = oppgaveRepository.hentOppgaver(behandlingId);
        sjekkForAktivOppgave(oppgaveHistorikk);
        var nyOppgave = oppgave(behandlingFpsak);
        oppgaveRepository.lagre(nyOppgave);
        opprettOppgaveEgenskaper(nyOppgave, behandlingFpsak);
        oppdaterOppgaveEventLogg(behandlingFpsak);
        videreførNyligUtløptReservasjon(oppgaveHistorikk, nyOppgave, behandlingFpsak);
        køStatistikk.lagre(nyOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        LOG.info("Gjenåpnet {} oppgaveId {}", SYSTEM, nyOppgave.getId());
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.GJENÅPNE_OPPGAVE;
    }

    private void videreførNyligUtløptReservasjon(List<Oppgave> eksisterendeOppgaver,
                                                 Oppgave nyOppgave,
                                                 BehandlingFpsak behandlingFpsak) {
        eksisterendeOppgaver.stream()
                .peek(o -> LOG.info("Ser på oppgaveId {}", o.getId()))
                .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
                .filter(o -> o.getBehandlendeEnhet().equals(behandlingFpsak.getBehandlendeEnhetId()))
                .map(Oppgave::getReservasjon)
                .filter(r -> r.getReservertTil().isAfter(LocalDateTime.now().minus(15, ChronoUnit.MINUTES)))
                .ifPresent(r -> {
                    LOG.info("Viderefører reservasjon");
                    reservasjonTjeneste.reserverBasertPåAvsluttetReservasjon(nyOppgave, r);
                });
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, BehandlingFpsak behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private void oppdaterOppgaveEventLogg(BehandlingFpsak behandlingFpsak) {
        var oel = OppgaveEventLogg.builder()
                .behandlingId(behandlingFpsak.getBehandlingId())
                .behandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .type(OppgaveEventType.GJENAPNET)
                .build();
        oppgaveRepository.lagre(oel);
    }

    private static void sjekkForAktivOppgave(List<Oppgave> eksisterendeOppgaver) {
        eksisterendeOppgaver.stream().filter(Oppgave::getAktiv).findAny().ifPresent(o -> {
            throw new IllegalStateException("Fant eksisterende oppgave, avslutter behandling av hendelse");
        });
    }

}
