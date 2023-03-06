package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.List;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter, String saksbehandler) {
        this.andreKriterier = aksjonspunkter.stream()
            .anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.OPPRETTET.equals(a.status())) ? List.of(
            AndreKriterierType.TIL_BESLUTTER) : List.of();
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
