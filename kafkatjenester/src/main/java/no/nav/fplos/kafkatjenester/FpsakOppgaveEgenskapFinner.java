package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private static final Logger log = LoggerFactory.getLogger(FpsakOppgaveEgenskapFinner.class);
    private final FpsakAksjonspunkt fpsakAksjonspunkt;
    private final OppgaveEventLogg sisteEvent;
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(BehandlingFpsak behandling,
                                      List<OppgaveEventLogg> eventHistorikk,
                                      List<Aksjonspunkt> aksjonspunkt) {
        this.fpsakAksjonspunkt = new FpsakAksjonspunkt(aksjonspunkt);
        this.sisteEvent = sisteOpprettetEventFra(eventHistorikk);
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (harGradering(behandling)) this.andreKriterier.add(AndreKriterierType.SOKT_GRADERING);
        if (erUtbetalingTilBruker(behandling)) this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        if (erVurderSykdom(behandling)) log.info("Aktuell for VURDER_SYKDOM-egenskap, disablet inntil videre"); //this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);

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

    public OppgaveEventLogg getSisteEvent() {
        return sisteEvent;
    }

    private static boolean erVurderSykdom(BehandlingFpsak behandling) {
        return behandling.getHarVurderSykdom() != null && behandling.getHarVurderSykdom();
    }

    private static OppgaveEventLogg sisteOpprettetEventFra(List<OppgaveEventLogg> logg) {
        return safeStream(logg)
                .filter(e -> e.getEventType().equals(OppgaveEventType.OPPRETTET))
                .findFirst()
                .orElse(null);
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
