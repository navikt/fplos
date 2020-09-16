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

        if (behandling.harGradering()) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (behandling.harVurderSykdom()) this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        if (behandling.erBerørtBehandling()) this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        if (erUtbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (behandling.erEndringssøknad()) this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);

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

    private static boolean erUtbetalingTilBruker(BehandlingFpsak behandling) {
        Boolean harRefusjonskrav = behandling.harRefusjonskravFraArbeidsgiver();
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        return harRefusjonskrav != null && !harRefusjonskrav;
    }
}
