package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving;

import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapFinner;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class TilbakekrevingOppgaveEgenskapFinner implements OppgaveEgenskapFinner {
    private final List<AndreKriterierType> andreKriterier;
    private final String saksbehandlerForTotrinn;

    public TilbakekrevingOppgaveEgenskapFinner(List<Aksjonspunkt> aksjonspunkter, String saksbehandler) {
        this.andreKriterier = fra(aksjonspunkter);
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    public TilbakekrevingOppgaveEgenskapFinner(BehandlingId behandlingId, List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter, String saksbehandler) {
        this.andreKriterier = aksjonspunkter.stream().anyMatch(a -> a.definisjon().equals("5005") && Aksjonspunktstatus.OPPRETTET.equals(a.status())) ?
            List.of(AndreKriterierType.TIL_BESLUTTER) : List.of();
        this.saksbehandlerForTotrinn = saksbehandler;
    }

    private List<AndreKriterierType> fra(List<Aksjonspunkt> aksjonspunkter) {
        if (aksjonspunkter.stream().anyMatch(a -> a.getKode().equals("5005") && a.erOpprettet())) {
            return Collections.singletonList(AndreKriterierType.TIL_BESLUTTER);
        }
        return Collections.emptyList();
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
