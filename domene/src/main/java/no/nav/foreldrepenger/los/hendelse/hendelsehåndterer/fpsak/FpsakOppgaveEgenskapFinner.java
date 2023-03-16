package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto.UtlandMarkering;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier = new ArrayList<>();
    private final String saksbehandlerForTotrinn;

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
        if (Ytelse.FORELDREPENGER.equals(behandling.ytelse()) && Behandlingstype.REVURDERING.equals(behandling.behandlingstype())
            && behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.SØKNAD::equals)) {
            this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (behandling.faresignaler()) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.KLAGE_TILBAKEBETALING::equals)) {
            this.andreKriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (fagsakHarEøsMarkering(behandling)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        var aksjonspunkter = behandling.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).toList();
        this.andreKriterier.addAll(FpsakAksjonspunktWrapper.getKriterier(aksjonspunkter, behandling.fagsakEgenskaper()));
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean fagsakHarEøsMarkering(LosBehandlingDto behandling) {
        return Optional.of(behandling)
            .map(LosBehandlingDto::fagsakEgenskaper)
            .map(LosFagsakEgenskaperDto::utlandMarkering)
            .map(UtlandMarkering.EØS_BOSATT_NORGE::equals)
            .orElse(false);
    }

}
