package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil.oppgave;
import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.NY_ENHET;

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
    public void håndter(BehandlingFpsak behandlingFpsak) {
        var eksisterendeOppgave = oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingFpsak.getBehandlingId())
                .orElseThrow(() -> new IllegalStateException("Fant ikke eksisterende oppgave"));
        var nyOppgave = lagOppgave(behandlingFpsak);
        flyttReservasjon(eksisterendeOppgave, nyOppgave);
        oppgaveTjeneste.avsluttOppgaveOgReservasjonUtenEventlogg(eksisterendeOppgave);
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
            var nyVarighetTil = gammelReservasjon.getReservertTil().plusHours(2);
            var reservasjon = reservasjonTjeneste.reserverOppgaveBasertPåEksisterendeReservasjon(nyOppgave, gammelReservasjon, nyVarighetTil);
            LOG.info("Oppretter ny forlenget reservasjonId {} varighet til {} reservert_av {}",
                    reservasjon.getId(), reservasjon.getReservertTil(), reservasjon.getReservertAv());
        } else if (erNyEnhet && aktivReservasjon) {
            reservasjonTjeneste.slettReservasjonMedEventLogg(gammelReservasjon, NY_ENHET);
            LOG.info("Overfører oppgave til ny enhet. Avslutter eksisterende reservasjon.");
        }
    }

    private Oppgave lagOppgave(BehandlingFpsak behandlingFpsak) {
        var nyOppgave = oppgave(behandlingFpsak);
        oppgaveTjeneste.lagre(nyOppgave);
        oppdaterOppgaveEgenskaper(nyOppgave, behandlingFpsak);
        return nyOppgave;
    }

    private void oppdaterOppgaveEgenskaper(Oppgave gjenåpnetOppgave, BehandlingFpsak behandlingFpsak) {
        var egenskapFinner = new FpsakOppgaveEgenskapFinner(behandlingFpsak);
        oppgaveEgenskapHåndterer.håndterOppgaveEgenskaper(gjenåpnetOppgave, egenskapFinner);
    }

}
