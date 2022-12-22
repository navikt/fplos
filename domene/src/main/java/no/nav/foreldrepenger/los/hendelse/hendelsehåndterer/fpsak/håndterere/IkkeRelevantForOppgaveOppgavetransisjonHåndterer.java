package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgavetransisjonHåndterer;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

@ApplicationScoped
public class IkkeRelevantForOppgaveOppgavetransisjonHåndterer implements FpsakOppgavetransisjonHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(IkkeRelevantForOppgaveOppgavetransisjonHåndterer.class);

    public IkkeRelevantForOppgaveOppgavetransisjonHåndterer() {
        // Cdi proxy. Gjør denne applicationscoped fremfor statisk for at den skal bli injisert som øvrige.
    }

    @Override
    public void håndter(BehandlingId behandlingId, LosBehandlingDto behandling) {
        LOG.info("Ikke relevant for oppgaver");
    }

    @Override
    public Oppgavetransisjon kanHåndtere() {
        return Oppgavetransisjon.IKKE_RELEVANT;
    }
}
