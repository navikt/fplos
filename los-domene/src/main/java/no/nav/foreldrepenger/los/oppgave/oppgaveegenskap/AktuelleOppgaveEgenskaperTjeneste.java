package no.nav.foreldrepenger.los.oppgave.oppgaveegenskap;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FpsakAksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.RisikovurderingTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class AktuelleOppgaveEgenskaperTjeneste {

    private RisikovurderingTjeneste risikovurderingTjeneste;

    public AktuelleOppgaveEgenskaperTjeneste() {
    }

    @Inject
    public AktuelleOppgaveEgenskaperTjeneste(RisikovurderingTjeneste risikovurderingTjeneste) {
        this.risikovurderingTjeneste = risikovurderingTjeneste;
    }

    public AktuelleOppgaveEgenskaperData egenskaperForFpsak(BehandlingFpsak behandlingFpsak) {
        List<AndreKriterierType> egenskaper = new ArrayList<>();

        if (behandlingFpsak.harGradering()) egenskaper.add(AndreKriterierType.SOKT_GRADERING);
        if (behandlingFpsak.harVurderSykdom()) egenskaper.add(AndreKriterierType.VURDER_SYKDOM);
        if (behandlingFpsak.erBerørtBehandling()) egenskaper.add(AndreKriterierType.BERØRT_BEHANDLING);
        if (erUtbetalingTilBruker(behandlingFpsak)) egenskaper.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (endringssøknad(behandlingFpsak)) egenskaper.add(AndreKriterierType.ENDRINGSSØKNAD);
        if (risikovurderingTjeneste.skalVurdereFaresignaler(behandlingFpsak.getBehandlingId())) {
            egenskaper.add(AndreKriterierType.VURDER_FARESIGNALER);
        }

        FpsakAksjonspunkt fpsakAksjonspunkt = new FpsakAksjonspunkt(behandlingFpsak.getAksjonspunkter());
        egenskaper.addAll(fpsakAksjonspunkt.getKriterier());
        return new AktuelleOppgaveEgenskaperData(behandlingFpsak.getAnsvarligSaksbehandler(), egenskaper);
    }

    private static boolean endringssøknad(BehandlingFpsak behandling) {
        // fpsak legger på endringssøknadtype også på førstegangsbehandlinger
        return behandling.getYtelseType() == FagsakYtelseType.FORELDREPENGER
                && behandling.getBehandlingType() == BehandlingType.REVURDERING
                && behandling.erEndringssøknad();
    }

    private static boolean erUtbetalingTilBruker(BehandlingFpsak behandling) {
        Boolean harRefusjonskrav = behandling.harRefusjonskravFraArbeidsgiver();
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        return harRefusjonskrav != null && !harRefusjonskrav;
    }

}
