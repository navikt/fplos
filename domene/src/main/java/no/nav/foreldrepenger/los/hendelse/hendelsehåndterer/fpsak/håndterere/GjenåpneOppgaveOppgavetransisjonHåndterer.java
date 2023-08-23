package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

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
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        var oppgaveHistorikk = oppgaveRepository.hentOppgaver(behandlingId);
        sjekkForAktivOppgave(oppgaveHistorikk);
        var nyOppgave = OppgaveUtil.oppgave(behandlingId, behandling);
        oppgaveRepository.lagre(nyOppgave);
        opprettOppgaveEgenskaper(nyOppgave, behandling);
        videreførNyligUtløptReservasjon(oppgaveHistorikk, nyOppgave, behandling, eventHistorikk);
        oppdaterOppgaveEventLogg(behandlingId, behandling);
        køStatistikk.lagre(nyOppgave, KøOppgaveHendelse.ÅPNET_OPPGAVE);
        LOG.info("Gjenåpnet {} oppgaveId {}", SYSTEM, nyOppgave.getId());
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.GJENÅPNE_OPPGAVE;
    }

    private void videreførNyligUtløptReservasjon(List<Oppgave> eksisterendeOppgaver, Oppgave nyOppgave, LosBehandlingDto behandling,
                                                 OppgaveHistorikk eventHistorikk) {
        var finnesreservasjon = eksisterendeOppgaver.stream()
            .max(Comparator.comparing(Oppgave::getOpprettetTidspunkt))
            .filter(o -> o.getBehandlendeEnhet().equals(behandling.behandlendeEnhetId()))
            .map(Oppgave::getReservasjon)
            .filter(r -> r.getReservertTil().isAfter(LocalDateTime.now().minus(15, ChronoUnit.MINUTES)));
        finnesreservasjon.ifPresent(r -> {
            LOG.info("Viderefører reservasjon");
            reservasjonTjeneste.reserverBasertPåAvsluttetReservasjon(nyOppgave, r);
        });
        if (finnesreservasjon.isEmpty() && behandling.ansvarligSaksbehandlerIdent() != null && eventHistorikk.erPåVent()) {
            reservasjonTjeneste.reserverOppgave(nyOppgave, behandling.ansvarligSaksbehandlerIdent());
        }
    }

    private void opprettOppgaveEgenskaper(Oppgave oppgave, LosBehandlingDto behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(oppgave, egenskapFinner);
    }

    private void oppdaterOppgaveEventLogg(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak) {
        var oel = OppgaveEventLogg.builder()
            .behandlingId(behandlingId)
            .behandlendeEnhet(behandlingFpsak.behandlendeEnhetId())
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
