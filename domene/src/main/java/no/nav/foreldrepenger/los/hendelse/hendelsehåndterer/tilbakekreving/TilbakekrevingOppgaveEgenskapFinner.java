package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.FagsakEgenskaper;
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
        this.andreKriterier = new ArrayList<>();
        if (FagsakEgenskaper.fagsakErMarkertBosattUtland(egenskaperDto)) {
            this.andreKriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertEØSBosattNorge(egenskaperDto)) {
            this.andreKriterier.add(AndreKriterierType.EØS_SAK);
        }
        if (FagsakEgenskaper.fagsakErMarkertSammensattKontroll(egenskaperDto)) {
            this.andreKriterier.add(AndreKriterierType.SAMMENSATT_KONTROLL);
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

}
