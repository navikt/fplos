package no.nav.foreldrepenger.los.oppgave.risikovurdering;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.OppgaveEgenskapTjeneste;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.Kontrollresultat;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.KontrollresultatWrapper;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.RisikoklassifiseringEntitet;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.modell.RisikoklassifiseringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class RisikovurderingTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(RisikovurderingTjeneste.class);

    private RisikoklassifiseringRepository risikoklassifiseringRepository;
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveEgenskapTjeneste oppgaveEgenskapTjeneste;

    public RisikovurderingTjeneste() {
        // CDI
    }

    @Inject
    public RisikovurderingTjeneste(RisikoklassifiseringRepository risikoklassifiseringRepository,
                                   OppgaveTjeneste oppgaveTjeneste, OppgaveEgenskapTjeneste oppgaveEgenskapTjeneste) {
        this.risikoklassifiseringRepository = risikoklassifiseringRepository;
        this.oppgaveTjeneste = oppgaveTjeneste;
        this.oppgaveEgenskapTjeneste = oppgaveEgenskapTjeneste;
    }

    public void lagreKontrollresultat(KontrollresultatWrapper resultatWrapper) {
        var behandlingId = resultatWrapper.getBehandlingId();
        lagre(resultatWrapper, behandlingId);
        oppgaveTjeneste.hentNyesteOppgaveTilknyttet(behandlingId)
                .filter(Oppgave::getAktiv)
                .ifPresent(o -> oppdaterOppgaveEgenskap(o, resultatWrapper));
        //oppgaveTjeneste.hentAktivOppgave(behandlingId).ifPresent(o -> oppdaterOppgaveEgenskap(o, resultatWrapper));
        LOG.info("Lagrer kontrollresultat {}", resultatWrapper.getKontrollresultatkode());
    }

    public boolean skalVurdereFaresignaler(BehandlingId behandlingId) {
        Objects.requireNonNull(behandlingId, "behandlingId");
        Optional<RisikoklassifiseringEntitet> resultat =
                risikoklassifiseringRepository.hentRisikoklassifiseringForBehandling(behandlingId.toUUID());
        return resultat.map(RisikoklassifiseringEntitet::getKontrollresultat)
                .map(RisikovurderingTjeneste::erHøyRisiko)
                .orElse(false);
    }

    private void oppdaterOppgaveEgenskap(Oppgave oppgave, KontrollresultatWrapper resultatWrapper) {
        boolean skalVurdereFaresignaler = erHøyRisiko(resultatWrapper.getKontrollresultatkode());
        LOG.info("Skal opprette vurdere faresignaler-egenskap for oppgaveId {}: {}", oppgave.getId(), skalVurdereFaresignaler);
        if (skalVurdereFaresignaler) {
            oppgaveEgenskapTjeneste.aktiverOppgaveEgenskap(oppgave, AndreKriterierType.VURDER_FARESIGNALER);
        } else {
            oppgaveEgenskapTjeneste.deaktiverOppgaveEgenskap(oppgave, AndreKriterierType.VURDER_FARESIGNALER);
        }
    }

    private static boolean erHøyRisiko(Kontrollresultat kontrollresultat) {
        return Objects.equals(kontrollresultat, Kontrollresultat.HØY);
    }

    private void lagre(KontrollresultatWrapper resultatWrapper, BehandlingId beh) {
        RisikoklassifiseringEntitet entitet = RisikoklassifiseringEntitet.builder()
                .medKontrollresultat(resultatWrapper.getKontrollresultatkode())
                .buildFor(beh);
        risikoklassifiseringRepository.lagreRisikoklassifisering(entitet, beh);
    }
}
