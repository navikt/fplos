package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(BehandlingFpsak behandling) {
        this.saksbehandlerForTotrinn = behandling.getAnsvarligSaksbehandler();

        if (behandling.harGradering()) {
            this.andreKriterier.add(AndreKriterierType.SØKT_GRADERING);
        }
        if (behandling.harVurderSykdom()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandling.erRevurderingPgaPleiepenger()) {
            this.andreKriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandling.erBerørtBehandling()) {
            this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (erUtbetalingTilBruker(behandling)) {
            this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (erEndringssøknad(behandling)) {
            this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (behandling.harFareSignaler()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }
        if (behandling.skalVurdereEøsOpptjening()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }

        var fpsakAksjonspunktWrapper = new FpsakAksjonspunktWrapper(behandling.getAksjonspunkt());
        andreKriterier.addAll(fpsakAksjonspunktWrapper.getKriterier());
    }

    public FpsakOppgaveEgenskapFinner(LosBehandlingDto behandling) {
        this.saksbehandlerForTotrinn = behandling.ansvarligSaksbehandlerIdent();

        if (Optional.ofNullable(behandling.foreldrepengerDto()).filter(LosBehandlingDto.LosForeldrepengerDto::gradering).isPresent()) {
            this.andreKriterier.add(AndreKriterierType.SØKT_GRADERING);
        }
        if (Optional.ofNullable(behandling.foreldrepengerDto()).filter(LosBehandlingDto.LosForeldrepengerDto::sykdomsvurdering).isPresent()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.PLEIEPENGER::equals)) {
            this.andreKriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.BERØRT::equals)) {
            this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (!behandling.refusjonskrav()) {
            this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.PLEIEPENGER::equals)) {
            this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (behandling.faresignaler()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }
        if (Optional.ofNullable(behandling.foreldrepengerDto()).filter(LosBehandlingDto.LosForeldrepengerDto::annenForelderRettEØS).isPresent()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        var aksjonspunkter = behandling.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).collect(Collectors.toList());
        var fpsakAksjonspunktWrapper = new FpsakAksjonspunktWrapper(aksjonspunkter);
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
