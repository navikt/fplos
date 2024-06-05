package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.felles.util.StreamUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FagsakEgenskaper;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto.FagsakMarkering;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final Set<AndreKriterierType> andreKriterier = new LinkedHashSet<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(LosBehandlingDto behandling) {
        this.saksbehandlerForTotrinn = behandling.ansvarligSaksbehandlerIdent();

        if (harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.SYKDOMSVURDERING)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.BARE_FAR_RETT)) {
            this.andreKriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.PLEIEPENGER::equals)) {
            this.andreKriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.BERØRT::equals)) {
            this.andreKriterier.add(AndreKriterierType.BERØRT_BEHANDLING);
        }
        if (!behandling.refusjonskrav() || harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.DIREKTE_UTBETALING)) {
            this.andreKriterier.add(AndreKriterierType.UTBETALING_TIL_BRUKER);
        }
        if (Ytelse.FORELDREPENGER.equals(behandling.ytelse()) && Behandlingstype.REVURDERING.equals(behandling.behandlingstype())
            && behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.SØKNAD::equals)) {
            this.andreKriterier.add(AndreKriterierType.ENDRINGSSØKNAD);
        }
        if (behandling.faresignaler() || harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.FARESIGNALER)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.KLAGE_TILBAKEBETALING::equals)) {
            this.andreKriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (FagsakEgenskaper.fagsakErMarkertEØSBosattNorge(behandling) || harSaksegenskap(behandling, LokalFagsakEgenskap.EØS_BOSATT_NORGE)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertBosattUtland(behandling) || harSaksegenskap(behandling, LokalFagsakEgenskap.BOSATT_UTLAND)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertSammensattKontroll(behandling)  || harSaksegenskap(behandling, LokalFagsakEgenskap.SAMMENSATT_KONTROLL)) {
            this.andreKriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (FagsakEgenskaper.fagsakErMarkertDød(behandling)  || harSaksegenskap(behandling, LokalFagsakEgenskap.DØD)) {
            this.andreKriterier.add(AndreKriterierType.DØD);
        }
        var aksjonspunkter = behandling.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).toList();

        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erTilBeslutter)) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erRegistrerPapirSøknad)) {
            this.andreKriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (skalVurdereBehovForSED(aksjonspunkter, behandling)) {
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
        if (harSaksegenskap(behandling, LokalFagsakEgenskap.PRAKSIS_UTSETTELSE) && matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalVurdereNæring)) {
            this.andreKriterier.add(AndreKriterierType.NÆRING);
        }
        if (FagsakEgenskaper.fagsakErMarkertUtsettelse(behandling)  || harSaksegenskap(behandling, LokalFagsakEgenskap.PRAKSIS_UTSETTELSE)) {
            this.andreKriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
    }

    public enum LokalFagsakEgenskap {
        NASJONAL, EØS_BOSATT_NORGE, BOSATT_UTLAND, SAMMENSATT_KONTROLL, DØD, NÆRING, PRAKSIS_UTSETTELSE
    }

    public enum LokalBehandlingEgenskap {
        SYKDOMSVURDERING, BARE_FAR_RETT, FARESIGNALER, DIREKTE_UTBETALING
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return new ArrayList<>(andreKriterier);
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean skalVurdereBehovForSED(List<Aksjonspunkt> aksjonspunkt, LosBehandlingDto dto) {
        var skalVurdereInnhentingAvSED = matchAksjonspunkt(aksjonspunkt, Aksjonspunkt::skalVurdereInnhentingAvSED);
        var fagsakErMarkertNasjonal = Optional.ofNullable(dto).map(LosBehandlingDto::fagsakEgenskaper)
            .map(LosFagsakEgenskaperDto::fagsakMarkering)
            .filter(FagsakMarkering.NASJONAL::equals)
            .isPresent() || harSaksegenskap(dto, LokalFagsakEgenskap.NASJONAL);
        return skalVurdereInnhentingAvSED && !fagsakErMarkertNasjonal;
    }

    private static boolean matchAksjonspunkt(List<Aksjonspunkt> aksjonspunkt, Predicate<Aksjonspunkt> predicate) {
        return StreamUtil.safeStream(aksjonspunkt).anyMatch(predicate);
    }

    private static boolean harSaksegenskap(LosBehandlingDto dto, LokalFagsakEgenskap egenskap) {
        return Optional.ofNullable(dto).map(LosBehandlingDto::saksegenskaper).orElse(List.of()).stream()
            .anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }

    private static boolean harBehandlingsegenskap(LosBehandlingDto dto, LokalBehandlingEgenskap egenskap) {
        return Optional.ofNullable(dto).map(LosBehandlingDto::behandlingsegenskaper).orElse(List.of()).stream()
            .anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }
}
