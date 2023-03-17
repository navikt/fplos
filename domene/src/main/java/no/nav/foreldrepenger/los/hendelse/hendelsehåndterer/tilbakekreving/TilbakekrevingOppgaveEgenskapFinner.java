package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto.UtlandMarkering;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter,
                                               String saksbehandler,
                                               LosFagsakEgenskaperDto egenskaperDto) {
        this.andreKriterier = new ArrayList<>();
        if (fagsakErMarkertUtland(egenskaperDto)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (fagsakErMarkertEØSBosattNorge(egenskaperDto)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (aksjonspunkter.stream().anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.OPPRETTET.equals(a.status()))) {
            this.andreKriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    @Override
    public List<AndreKriterierType> getAndreKriterier() {
        return andreKriterier;
    }

    @Override
    public String getSaksbehandlerForTotrinn() {
        return saksbehandlerForTotrinn;
    }

    private static boolean fagsakErMarkertEØSBosattNorge(LosFagsakEgenskaperDto egenskaperDto) {
        return Optional.ofNullable(egenskaperDto)
            .map(LosFagsakEgenskaperDto::utlandMarkering)
            .filter(UtlandMarkering.EØS_BOSATT_NORGE::equals)
            .isPresent();
    }

    private static boolean fagsakErMarkertUtland(LosFagsakEgenskaperDto egenskaperDto) {
        return Optional.ofNullable(egenskaperDto)
            .map(LosFagsakEgenskaperDto::utlandMarkering)
            .filter(UtlandMarkering.BOSATT_UTLAND::equals)
            .isPresent();
    }
}
