package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(value = "håndter.hendelse", firstDelay = 10, thenDelay = 10)
public class HåndterHendelseTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HåndterHendelseTask.class);

    public static final String HENDELSE_ID = "hendelseId";

    private HendelseRepository hendelseRepository;

    private TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer;
    private FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer;

    @Inject
    public HåndterHendelseTask(HendelseRepository hendelseRepository,
                               TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer,
                               FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer) {
        this.hendelseRepository = hendelseRepository;
        this.tilbakekrevingHendelseHåndterer = tilbakekrevingHendelseHåndterer;
        this.fpsakOppgaveHendelseHåndterer = fpsakOppgaveHendelseHåndterer;
    }

    HåndterHendelseTask() {
        //CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(HENDELSE_ID));
        var hendelse = hendelseRepository.hent(hendelseId);
        LOG.info("Håndterer hendelse for behandling {}", hendelse.getBehandlingId());

        switch (hendelse.getFagsystem()) {
            case FPSAK -> fpsakOppgaveHendelseHåndterer.håndter(hendelse);
            case FPTILBAKE -> tilbakekrevingHendelseHåndterer.håndter((TilbakekrevingHendelse) hendelse);
            default -> throw new IllegalStateException("Ukjent fagsystem " + hendelse.getFagsystem());
        }

        hendelseRepository.slett(hendelse);
    }
}
