package no.nav.fplos.kafkatjenester;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(BehandlingFpsak behandling,
                                      List<Aksjonspunkt> aksjonspunkt) {
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (harGradering(behandling)) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (erUtbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (erVurderSykdom(behandling)) this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);

        FpsakAksjonspunkt fpsakAksjonspunkt = new FpsakAksjonspunkt(aksjonspunkt);
        andreKriterier.addAll(fpsakAksjonspunkt.getKriterier());
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean erVurderSykdom(BehandlingFpsak behandling) {
        return behandling.getHarVurderSykdom() != null && behandling.getHarVurderSykdom();
    }

    private static boolean erUtbetalingTilBruker(BehandlingFpsak behandling) {
        Boolean harRefusjonskrav = behandling.getHarRefusjonskravFraArbeidsgiver();
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        return harRefusjonskrav != null && !harRefusjonskrav;
    }

    private static boolean harGradering(BehandlingFpsak behandling) {
        return behandling.getHarGradering() != null && behandling.getHarGradering();
    }
}
