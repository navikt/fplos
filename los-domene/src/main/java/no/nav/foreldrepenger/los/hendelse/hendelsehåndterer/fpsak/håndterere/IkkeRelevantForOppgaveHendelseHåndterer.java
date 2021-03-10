package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakHendelseHåndterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IkkeRelevantForOppgaveHendelseHåndterer implements FpsakHendelseHåndterer {
    private static final Logger LOG = LoggerFactory.getLogger(IkkeRelevantForOppgaveHendelseHåndterer.class);

    @Override
    public void håndter() {
        LOG.info("Ikke relevant for oppgaver");
    }
}
