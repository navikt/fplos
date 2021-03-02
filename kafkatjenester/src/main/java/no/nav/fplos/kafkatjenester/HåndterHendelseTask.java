package no.nav.fplos.kafkatjenester;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.loslager.hendelse.Fagsystem;
import no.nav.foreldrepenger.loslager.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.loslager.repository.HendelseRepository;
import no.nav.fplos.domenetjenester.hendelsehåndterer.ForeldrepengerHendelseHåndterer;
import no.nav.fplos.domenetjenester.hendelsehåndterer.TilbakekrevingHendelseHåndterer;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(HåndterHendelseTask.TASKTYPE)
public class HåndterHendelseTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "håndter.hendelse";
    public static final String HENDELSE_ID = "hendelseId";

    private HendelseRepository hendelseRepository;

    private ForeldrepengerHendelseHåndterer foreldrepengerHendelseHåndterer;
    private TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer;

    @Inject
    public HåndterHendelseTask(HendelseRepository hendelseRepository,
                               ForeldrepengerHendelseHåndterer foreldrepengerHendelseHåndterer,
                               TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer) {
        this.hendelseRepository = hendelseRepository;
        this.foreldrepengerHendelseHåndterer = foreldrepengerHendelseHåndterer;
        this.tilbakekrevingHendelseHåndterer = tilbakekrevingHendelseHåndterer;
    }

    HåndterHendelseTask() {
        //CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var hendelseId = Long.parseLong(prosessTaskData.getPropertyValue(HENDELSE_ID));
        var hendelse = hendelseRepository.hent(hendelseId);

        if (Objects.equals(hendelse.getFagsystem(), Fagsystem.FPSAK)) {
            foreldrepengerHendelseHåndterer.håndter(hendelse);
        } else if (Objects.equals(hendelse.getFagsystem(), Fagsystem.FPTILBAKE)) {
            tilbakekrevingHendelseHåndterer.håndter((TilbakekrevingHendelse) hendelse);

        } else {
            throw new IllegalStateException("Ukjent fagsystem " + hendelse.getFagsystem());
        }

        hendelseRepository.slett(hendelse);
    }
}
