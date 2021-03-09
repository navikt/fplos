package no.nav.foreldrepenger.los.statistikk.statistikk_ny;

import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltreringKnytning;

import java.util.List;
import java.util.stream.Stream;

public class OppgaveknytningerFørEtterOppdatering {
    private List<OppgaveFiltreringKnytning> knytningerFørOppdatering;
    private List<OppgaveFiltreringKnytning> knytningerEtterOppdatering;

    public OppgaveknytningerFørEtterOppdatering() {
    }

    public void setKnytningerFørOppdatering(List<OppgaveFiltreringKnytning> knytningerFørOppdatering) {
        this.knytningerFørOppdatering = knytningerFørOppdatering;
    }

    public void setKnytningerEtterOppdatering(List<OppgaveFiltreringKnytning> knytningerEtterOppdatering) {
        this.knytningerEtterOppdatering = knytningerEtterOppdatering;
    }

    public Stream<OppgaveFiltreringKnytning> getUtAvKø() {
        return knytningerFørOppdatering.stream().filter(ok -> !knytningerEtterOppdatering.contains(ok));
    }

    public Stream<OppgaveFiltreringKnytning> getInnPåKø() {
        return knytningerEtterOppdatering.stream().filter(ok -> !knytningerFørOppdatering.contains(ok));
    }

}
