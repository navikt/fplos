package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.NY_ENHET;
import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_VIDEREFØRT_NY_OPPGAVE;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class OppdaterOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OppdaterOppgaveOppgavetransisjonHåndterer.class);
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveTjeneste oppgaveTjeneste;
    @Inject
    public OppdaterOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                     ReservasjonTjeneste reservasjonTjeneste,
                                                     OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveTjeneste = oppgaveTjeneste;
    }

    public OppdaterOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling, OppgaveHistorikk eventHistorikk) {
        var eksisterendeOppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId)
            .orElseThrow(() -> new IllegalStateException("Fant ikke eksisterende oppgave"));
        var nyOppgave = lagOppgave(behandlingId, behandling);
        flyttReservasjon(eksisterendeOppgave, nyOppgave);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(eksisterendeOppgave, OppgaveEventType.GJENAPNET, RESERVASJON_VIDEREFØRT_NY_OPPGAVE);
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.OPPDATER_OPPGAVE;
    }

    private void flyttReservasjon(Oppgave eksisterendeOppgave, Oppgave nyOppgave) {
        var gammelReservasjon = eksisterendeOppgave.getReservasjon();
        boolean erNyEnhet = !eksisterendeOppgave.getBehandlendeEnhet().equals(nyOppgave.getBehandlendeEnhet());
        boolean aktivReservasjon = gammelReservasjon != null && gammelReservasjon.erAktiv();
        if (!erNyEnhet && aktivReservasjon) {
            var eksisterendeVarighetTil = gammelReservasjon.getReservertTil();
            var kandidatVarighetTil = LocalDateTime.now().plusHours(2);
            var nyVarighetTil = kandidatVarighetTil.isAfter(eksisterendeVarighetTil) ? kandidatVarighetTil : eksisterendeVarighetTil;
            var reservasjon = reservasjonTjeneste.reserverOppgaveBasertPåEksisterendeReservasjon(nyOppgave, gammelReservasjon, nyVarighetTil);
            LOG.info("Oppretter ny forlenget reservasjonId {} varighet til {} reservert_av {}", reservasjon.getId(), reservasjon.getReservertTil(),
                reservasjon.getReservertAv());
        } else if (erNyEnhet && aktivReservasjon) {
            reservasjonTjeneste.slettReservasjonMedEventLogg(gammelReservasjon, NY_ENHET);
            LOG.info("Overfører oppgave til ny enhet. Avslutter eksisterende reservasjon.");
        }
    }

    private Oppgave lagOppgave(BehandlingId behandlingId, LosBehandlingDto behandlingFpsak) {
        var nyOppgave = OppgaveUtil.oppgave(behandlingId, behandlingFpsak);
        oppgaveTjeneste.lagre(nyOppgave);
        oppdaterOppgaveEgenskaper(nyOppgave, behandlingFpsak);
        return nyOppgave;
    }

    private void oppdaterOppgaveEgenskaper(Oppgave gjenåpnetOppgave, LosBehandlingDto behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
    }

}
