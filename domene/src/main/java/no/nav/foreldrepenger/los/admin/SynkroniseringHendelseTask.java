package no.nav.foreldrepenger.los.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandling;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(SynkroniseringHendelseTask.TASKTYPE)
public class SynkroniseringHendelseTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "synkronisering.hendelse";
    public static final String BEHANDLING_ID_TASK_KEY = "behandlingId";

    private ForeldrepengerBehandling behandlingKlient;
    private FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer;

    @Inject
    public SynkroniseringHendelseTask(ForeldrepengerBehandling behandlingKlient,
            FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer) {
        this.behandlingKlient = behandlingKlient;
        this.fpsakOppgaveHendelseHåndterer = fpsakOppgaveHendelseHåndterer;
    }

    public SynkroniseringHendelseTask() {
        // CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingId = prosessTaskData.getPropertyValue(BEHANDLING_ID_TASK_KEY);
        var behandlingDto = behandlingKlient.hentUtvidetBehandlingDto(behandlingId);
        var fagsakDto = behandlingKlient.hentFagsak(behandlingDto.links());

        var hendelse = new Hendelse();
        hendelse.setFagsystem(Fagsystem.FPSAK);
        hendelse.setBehandlingId(BehandlingId.fromUUID(behandlingDto.uuid()));
        hendelse.setSaksnummer(fagsakDto.saksnummer());
        hendelse.setBehandlendeEnhet(behandlingDto.behandlendeEnhetId());
        hendelse.setAktørId(fagsakDto.aktørId());
        hendelse.setBehandlingOpprettetTidspunkt(behandlingDto.opprettet());
        hendelse.setBehandlingType(behandlingDto.type());
        hendelse.setYtelseType(fagsakDto.fagsakYtelseType());

        fpsakOppgaveHendelseHåndterer.håndter(hendelse);
    }
}
