package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.NY_ENHET;
import static no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter.RESERVASJON_VIDEREFØRT_NY_OPPGAVE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringKnytning;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøOppgaveHendelse;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class OppdaterOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(OppdaterOppgaveOppgavetransisjonHåndterer.class);
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveTjeneste oppgaveTjeneste;
    private KøStatistikkTjeneste køStatistikkTjeneste;

    @Inject
    public OppdaterOppgaveOppgavetransisjonHåndterer(OppgaveTjeneste oppgaveTjeneste,
                                                     ReservasjonTjeneste reservasjonTjeneste,
                                                     OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer,
                                                     KøStatistikkTjeneste køStatistikkTjeneste) {
        this.reservasjonTjeneste = reservasjonTjeneste;
        this.oppgaveEgenskapHåndterer = oppgaveEgenskapHåndterer;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.køStatistikkTjeneste = køStatistikkTjeneste;
    }

    public OppdaterOppgaveOppgavetransisjonHåndterer() {
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling) {
        var eksisterendeOppgave = oppgaveTjeneste.hentAktivOppgave(behandlingId)
                .orElseThrow(() -> new IllegalStateException("Fant ikke eksisterende oppgave"));
        var nyOppgave = lagOppgave(behandlingId, behandling);
        vedlikeholdKøStatistikk(eksisterendeOppgave, nyOppgave);
        flyttReservasjon(eksisterendeOppgave, nyOppgave);
        oppgaveTjeneste.avsluttOppgaveMedEventLogg(eksisterendeOppgave, OppgaveEventType.GJENAPNET, RESERVASJON_VIDEREFØRT_NY_OPPGAVE);
    }

    private void vedlikeholdKøStatistikk(Oppgave eksisterendeOppgave, Oppgave nyOppgave) {
        // kan erstattes med query basert på oppgavebeholdning når beholdningen er bygget opp for tilbakekreving og fpsak-oppgaver
        var køKnytningerGammelOppgave = køStatistikkTjeneste.hentOppgaveFiltreringKnytningerForOppgave(eksisterendeOppgave);
        var køKnytningerNyOppgave = køStatistikkTjeneste.hentOppgaveFiltreringKnytningerForOppgave(nyOppgave);
        var knytninger = Stream.concat(køKnytningerGammelOppgave.stream(), køKnytningerNyOppgave.stream())
                .collect(Collectors.groupingBy(OppgaveFiltreringKnytning::oppgaveId,
                        Collectors.mapping(OppgaveFiltreringKnytning::oppgaveFiltreringId, Collectors.toList())));
        var utAvKø = Optional.ofNullable(knytninger.get(eksisterendeOppgave.getId())).orElse(List.of());
        var innPåKø = Optional.ofNullable(knytninger.get(nyOppgave.getId())).orElse(List.of());
        if (!utAvKø.isEmpty() || !innPåKø.isEmpty()) {
            utAvKø = new ArrayList<>(utAvKø);
            innPåKø = new ArrayList<>(innPåKø);
            utAvKø.removeAll(innPåKø);
            innPåKø.removeAll(Optional.ofNullable(knytninger.get(eksisterendeOppgave.getId())).orElse(List.of()));
        }
        LOG.info("Køstatistikk-knytninger mellom oppgaveId og oppgaveFiltreringId: {}. På vei ut av køer {}, på vei inn i køer {}",
                knytninger, utAvKø, innPåKø);
        innPåKø.forEach(i -> køStatistikkTjeneste.lagre(nyOppgave, i, KøOppgaveHendelse.INN_FRA_ANNEN_KØ));
        utAvKø.forEach(i -> køStatistikkTjeneste.lagre(nyOppgave, i, KøOppgaveHendelse.UT_TIL_ANNEN_KØ));
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
            var nyVarighetTil = kandidatVarighetTil.isAfter(eksisterendeVarighetTil)
                    ? kandidatVarighetTil
                    : eksisterendeVarighetTil;
            var reservasjon = reservasjonTjeneste.reserverOppgaveBasertPåEksisterendeReservasjon(nyOppgave,
                    gammelReservasjon, nyVarighetTil);
            LOG.info("Oppretter ny forlenget reservasjonId {} varighet til {} reservert_av {}",
                    reservasjon.getId(), reservasjon.getReservertTil(), reservasjon.getReservertAv());
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
