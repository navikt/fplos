package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(BehandlingFpsak behandling) {
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (behandling.harGradering()) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (behandling.harVurderSykdom()) this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        if (behandling.erBerørtBehandling()) this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        if (erUtbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (erEndringssøknad(behandling)) this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);

        var fpsakAksjonspunktWrapper = new FpsakAksjonspunktWrapper(behandling.getAksjonspunkter());
        andreKriterier.addAll(fpsakAksjonspunktWrapper.getKriterier());
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean erEndringssøknad(BehandlingFpsak behandling) {
        // fpsak legger på endringssøknadtype også på førstegangsbehandlinger
        return behandling.getYtelseType() == FagsakYtelseType.FORELDREPENGER
                && behandling.getBehandlingType() == BehandlingType.REVURDERING
                && behandling.erEndringssøknad();
    }

    private static boolean erUtbetalingTilBruker(BehandlingFpsak behandling) {
        var harRefusjonskrav = behandling.harRefusjonskravFraArbeidsgiver();
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        return harRefusjonskrav != null && !harRefusjonskrav;
    }
}
