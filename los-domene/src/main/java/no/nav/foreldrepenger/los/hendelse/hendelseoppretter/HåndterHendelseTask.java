package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveHendelseHåndtererFactory;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(HåndterHendelseTask.TASKTYPE)
public class HåndterHendelseTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HåndterHendelseTask.class);

    public static final String TASKTYPE = "håndter.hendelse";
    public static final String HENDELSE_ID = "hendelseId";

    private HendelseRepository hendelseRepository;

    private TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer;
    private OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory;

    @Inject
    public HåndterHendelseTask(HendelseRepository hendelseRepository,
                               TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer,
                               OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory) {
        this.hendelseRepository = hendelseRepository;
        this.tilbakekrevingHendelseHåndterer = tilbakekrevingHendelseHåndterer;
        this.oppgaveHendelseHåndtererFactory = oppgaveHendelseHåndtererFactory;
    }

    HåndterHendelseTask() {
        //CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(HENDELSE_ID));
        var hendelse = hendelseRepository.hent(hendelseId);
        LOG.info("Håndterer hendelse for behandling {}", hendelse.getBehandlingId());

        if (Objects.equals(hendelse.getFagsystem(), Fagsystem.FPSAK)) {
            var håndterer = oppgaveHendelseHåndtererFactory.lagHåndterer(hendelse);
            håndterer.håndter();
        } else if (Objects.equals(hendelse.getFagsystem(), Fagsystem.FPTILBAKE)) {
            tilbakekrevingHendelseHåndterer.håndter((TilbakekrevingHendelse) hendelse);

        } else {
            throw new IllegalStateException("Ukjent fagsystem " + hendelse.getFagsystem());
        }

        hendelseRepository.slett(hendelse);
    }
}
