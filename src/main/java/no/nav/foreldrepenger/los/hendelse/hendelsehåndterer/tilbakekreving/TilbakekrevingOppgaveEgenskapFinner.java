package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.LokalFagsakEgenskap;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter,
                                               String saksbehandler,
                                               LosFagsakEgenskaperDto egenskaperDto,
                                               Collection<String> behandlingsegenskaper) {
        var saksegenskaper = Optional.ofNullable(egenskaperDto).map(LosFagsakEgenskaperDto::saksegenskaper).orElse(List.of());
        this.andreKriterier = new ArrayList<>();
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.BOSATT_UTLAND)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.EØS_BOSATT_NORGE)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.SAMMENSATT_KONTROLL)) {
            this.andreKriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.DØD)) {
            this.andreKriterier.add(AndreKriterierType.DØD);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.BARE_FAR_RETT)) {
            this.andreKriterier.add(AndreKriterierType.BARE_FAR_RETT);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.NÆRING)) {
            this.andreKriterier.add(AndreKriterierType.NÆRING);
        }
        if (harSaksegenskap(saksegenskaper, LokalFagsakEgenskap.PRAKSIS_UTSETTELSE)) {
            this.andreKriterier.add(AndreKriterierType.PRAKSIS_UTSETTELSE);
        }
        if (aktivtBeslutterAp(aksjonspunkter) && !aktiveApForutenBeslutterEllerVent(aksjonspunkter)) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (avbruttBeslutterAp(aksjonspunkter)) {
            this.andreKriterier.add(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        }
        if (harBehandlingsegenskap(behandlingsegenskaper, LokalBehandlingEgenskap.OVER_FIRE_RETTSGEBYR)) {
            this.andreKriterier.add(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
        }
        if (!harBehandlingsegenskap(behandlingsegenskaper, LokalBehandlingEgenskap.VARSLET)) {
            this.andreKriterier.add(AndreKriterierType.IKKE_VARSLET);
        }
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    public enum LokalBehandlingEgenskap {
        VARSLET, OVER_FIRE_RETTSGEBYR
    }

    private static boolean aktivtBeslutterAp(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean avbruttBeslutterAp(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.AVBRUTT.equals(a.status()));
    }

    private static boolean aktiveApForutenBeslutterEllerVent(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter) {
        return aksjonspunkter.stream()
            .anyMatch(a -> !Set.of("5005", "7001", "7002").contains(a.definisjon()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()));
    }

    private static boolean harSaksegenskap(List<String> saksegenskaper, LokalFagsakEgenskap egenskap) {
        return saksegenskaper.stream().anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }

    private static boolean harBehandlingsegenskap(Collection<String> behandlingsegenskaper, LokalBehandlingEgenskap egenskap) {
        return behandlingsegenskaper.stream().anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

}
