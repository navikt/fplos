package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.ArrayList;
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
                                               LosFagsakEgenskaperDto egenskaperDto) {
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
        if (aksjonspunkter.stream().anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.OPPRETTET.equals(a.status())) &&
            aksjonspunkter.stream().noneMatch(a -> !Set.of("5005","7001","7002").contains(a.definisjon()) && Aksjonspunktstatus.OPPRETTET.equals(a.status()))) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    private static boolean harSaksegenskap(List<String> saksegenskaper, LokalFagsakEgenskap egenskap) {
        return saksegenskaper.stream().anyMatch(s -> s.equalsIgnoreCase(egenskap.name()));
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
