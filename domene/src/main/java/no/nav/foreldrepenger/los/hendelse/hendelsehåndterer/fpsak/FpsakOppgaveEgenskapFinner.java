package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FagsakEgenskaper;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto.FagsakMarkering;

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
        if (FagsakEgenskaper.fagsakErMarkertEØSBosattNorge(behandling)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertBosattUtland(behandling)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertSammensattKontroll(behandling)) {
            this.andreKriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (FagsakEgenskaper.fagsakErMarkertDød(behandling)) {
            this.andreKriterier.add(AndreKriterierType.DØD);
        }
        var aksjonspunkter = behandling.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).toList();

        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erTilBeslutter)) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erRegistrerPapirSøknad)) {
            this.andreKriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (skalVurdereBehovForSED(aksjonspunkter, behandling.fagsakEgenskaper())) {
            this.andreKriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalVurdereArbeidInntekt)) {
            this.andreKriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erVurderFormkrav)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }
        // Legger på egenskap næring kun for aksjonspunkt i Opptjening og Beregning for det som er har oppgitt egen næring.
        if (FagsakEgenskaper.fagsakErMarkertNæring(behandling) && matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalVurdereNæring)) {
            this.andreKriterier.add(AndreKriterierType.NÆRING);
        }
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean skalVurdereBehovForSED(List<Aksjonspunkt> aksjonspunkt, LosFagsakEgenskaperDto dto) {
        var skalVurdereInnhentingAvSED = matchAksjonspunkt(aksjonspunkt, Aksjonspunkt::skalVurdereInnhentingAvSED);
        var fagsakErMarkertNasjonal = Optional.ofNullable(dto)
            .map(LosFagsakEgenskaperDto::fagsakMarkering)
            .filter(FagsakMarkering.NASJONAL::equals)
            .isPresent();
        return skalVurdereInnhentingAvSED && !fagsakErMarkertNasjonal;
    }

    private static boolean matchAksjonspunkt(List<Aksjonspunkt> aksjonspunkt, Predicate<Aksjonspunkt> predicate) {
        return safeStream(aksjonspunkt).anyMatch(predicate);
    }
}
