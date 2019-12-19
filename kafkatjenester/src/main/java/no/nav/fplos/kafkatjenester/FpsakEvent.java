package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;

import java.util.ArrayList;
import java.util.List;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

public class FpsakEvent {
    private final FpsakAksjonspunkt fpsakAksjonspunkt;
    private final OppgaveEventLogg sisteEvent;
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakEvent(BehandlingFpsak behandling,
                      List<OppgaveEventLogg> eventHistorikk,
                      List<Aksjonspunkt> aksjonspunkt) {
        this.fpsakAksjonspunkt = new FpsakAksjonspunkt(aksjonspunkt);
        this.sisteEvent = sisteOpprettetEventFra(eventHistorikk);
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (harGradering(behandling)) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (utbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (erOverførtGrunnetSykdom(behandling)) this.andreKriterier.add(AndreKriterierType.OVERFØRING_GRUNNET_SYKDOM);

        andreKriterier.addAll(fpsakAksjonspunkt.getKriterier());
    }

    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    public OppgaveEventLogg getSisteEvent() {
        return sisteEvent;
    }

    private static boolean erOverførtGrunnetSykdom(BehandlingFpsak behandling) {
        return behandling.getHarOverføringPgaSykdom() != null && behandling.getHarOverføringPgaSykdom();
    }

    private static OppgaveEventLogg sisteOpprettetEventFra(List<OppgaveEventLogg> logg) {
        return safeStream(logg)
                .filter(e -> e.getEventType().equals(OppgaveEventType.OPPRETTET))
                .findFirst()
                .orElse(null);
    }

    private static boolean utbetalingTilBruker(BehandlingFpsak behandling) {
        Boolean harRefusjonskrav = behandling.getHarRefusjonskravFraArbeidsgiver();
        //Skal ikke ha egenskap når harRefusjonskrav er true eller null. Vi avventer inntektsmelding før vi legger på egenskapen.
        return harRefusjonskrav != null && !harRefusjonskrav;
    }

    private static boolean harGradering(BehandlingFpsak behandling) {
        return behandling.getHarGradering() != null && behandling.getHarGradering();
    }
}
