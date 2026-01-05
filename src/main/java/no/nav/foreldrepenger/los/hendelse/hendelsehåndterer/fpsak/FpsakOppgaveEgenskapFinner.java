package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.felles.util.StreamUtil;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.LokalFagsakEgenskap;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class FpsakOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final Set<AndreKriterierType> andreKriterier = new LinkedHashSet<>();
    private final String saksbehandlerForTotrinn;

    public FpsakOppgaveEgenskapFinner(LosBehandlingDto behandling) {
        this.saksbehandlerForTotrinn = behandling.ansvarligSaksbehandlerIdent();
        var saksegenskaper = Optional.ofNullable(behandling.saksegenskaper()).orElse(List.of());

        if (harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.SYKDOMSVURDERING)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_SYKDOM);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.BARE_FAR_RETT)) {
            this.andreKriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.HASTER)) {
            this.andreKriterier.add(AndreKriterierType.HASTER);
        }
        if (harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.MOR_UKJENT_UTLAND)) {
            this.andreKriterier.add(AndreKriterierType.MOR_UKJENT_UTLAND);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.PLEIEPENGER::equals)) {
            this.andreKriterier.add(AndreKriterierType.PLEIEPENGER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.UTSATT_START::equals)) {
            this.andreKriterier.add(AndreKriterierType.UTSATT_START);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.OPPHØR_NY_SAK::equals)) {
            this.andreKriterier.add(AndreKriterierType.NYTT_VEDTAK);
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
        if (Behandlingstype.REVURDERING.equals(behandling.behandlingstype())
            && behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.INNTEKTSMELDING::equals)
            && behandling.behandlingsårsaker().stream().allMatch(Behandlingsårsak.INNTEKTSMELDING::equals)) {
            this.andreKriterier.add(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
        }
        if (behandling.faresignaler() || harBehandlingsegenskap(behandling, LokalBehandlingEgenskap.FARESIGNALER)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
        }
        if (behandling.behandlingsårsaker().stream().anyMatch(Behandlingsårsak.KLAGE_TILBAKEBETALING::equals)) {
            this.andreKriterier.add(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.EØS_BOSATT_NORGE)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.BOSATT_UTLAND)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.SAMMENSATT_KONTROLL)) {
            this.andreKriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.DØD)) {
            this.andreKriterier.add(AndreKriterierType.DØD);
        }
        var aksjonspunkter = behandling.aksjonspunkt().stream().map(Aksjonspunkt::aksjonspunktFra).toList();

        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erTilBeslutter)) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erReturnertFraBeslutter)) {
            this.andreKriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erRegistrerPapirSøknad)) {
            this.andreKriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (skalVurdereBehovForSED(aksjonspunkter, saksegenskaper)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalKontrollereTerminbekreftelse)) {
            this.andreKriterier.add(AndreKriterierType.TERMINBEKREFTELSE);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalVurdereArbeidInntekt)) {
            this.andreKriterier.add(AndreKriterierType.ARBEID_INNTEKT);
        }
        if (matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::erVurderFormkrav)) {
            this.andreKriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }
        // Legger på egenskap næring kun for aksjonspunkt i Opptjening og Beregning for det som er har oppgitt egen næring.
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.NÆRING) && matchAksjonspunkt(aksjonspunkter, Aksjonspunkt::skalVurdereNæring)) {
            this.andreKriterier.add(AndreKriterierType.NÆRING);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.PRAKSIS_UTSETTELSE)) {
            this.andreKriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
    }

    public enum LokalBehandlingEgenskap {
        SYKDOMSVURDERING, MOR_UKJENT_UTLAND, FARESIGNALER, DIREKTE_UTBETALING
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return new ArrayList<>(andreKriterier);
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean skalVurdereBehovForSED(List<Aksjonspunkt> aksjonspunkt, List<String> saksegenskaper) {
        if (matchAksjonspunkt(aksjonspunkt, Aksjonspunkt::skalVurdereInnhentingAvSED)) {
            return harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.EØS_BOSATT_NORGE) ||
                harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.BOSATT_UTLAND);
        } else {
            return false;
        }
    }

    private static boolean harSaksegenskap(List<String> saksegenskaper, LokalFagsakEgenskap egenskap) {
        return saksegenskaper.stream().anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }

    private static boolean matchAksjonspunkt(List<Aksjonspunkt> aksjonspunkt, Predicate<Aksjonspunkt> predicate) {
        return StreamUtil.safeStream(aksjonspunkt).anyMatch(predicate);
    }


    private static boolean harBehandlingsegenskap(LosBehandlingDto dto, LokalBehandlingEgenskap egenskap) {
        return Optional.ofNullable(dto).map(LosBehandlingDto::behandlingsegenskaper).orElse(List.of()).stream()
            .anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }

}
