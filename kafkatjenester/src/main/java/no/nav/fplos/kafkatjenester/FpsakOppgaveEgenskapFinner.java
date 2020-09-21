package no.nav.fplos.kafkatjenester;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(BehandlingFpsak behandling) {
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (behandling.harGradering()) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (behandling.harVurderSykdom()) this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        if (behandling.erBerørtBehandling()) this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        if (erUtbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (endringssøknad(behandling)) this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);

        FpsakAksjonspunkt fpsakAksjonspunkt = new FpsakAksjonspunkt(behandling.getAksjonspunkter());
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
