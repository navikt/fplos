package no.nav.foreldrepenger.los.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveHendelseHåndtererFactory;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandling;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsaker;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.jersey.Jersey;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@ApplicationScoped
@ProsessTask(SynkroniseringHendelseTask.TASKTYPE)
public class SynkroniseringHendelseTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "synkronisering.hendelse";
    public static final String BEHANDLING_ID_TASK_KEY = "behandlingId";

    private ForeldrepengerBehandling behandlingKlient;
    private ForeldrepengerFagsaker fagsakKlient;
    private OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory;

    @Inject
    public SynkroniseringHendelseTask(@Jersey ForeldrepengerBehandling behandlingKlient,
            @Jersey ForeldrepengerFagsaker fagsakKlient,
            OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory) {
        this.behandlingKlient = behandlingKlient;
        this.fagsakKlient = fagsakKlient;
        this.oppgaveHendelseHåndtererFactory = oppgaveHendelseHåndtererFactory;
    }

    public SynkroniseringHendelseTask() {
        // CDI
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var behandlingId = prosessTaskData.getPropertyValue(BEHANDLING_ID_TASK_KEY);
        var behandlingDto = behandlingKlient.hentUtvidetBehandlingDto(behandlingId);
        var fagsakDto = hentFagsakDto(behandlingDto);

        var hendelse = new Hendelse();
        hendelse.setFagsystem(Fagsystem.FPSAK);
        hendelse.setBehandlingId(BehandlingId.fromUUID(behandlingDto.uuid()));
        hendelse.setSaksnummer(fagsakDto.saksnummer());
        hendelse.setBehandlendeEnhet(behandlingDto.behandlendeEnhetId());
        hendelse.setAktørId(fagsakDto.aktørId());
        hendelse.setBehandlingOpprettetTidspunkt(behandlingDto.opprettet());
        hendelse.setBehandlingType(behandlingDto.type());
        hendelse.setYtelseType(fagsakDto.fagsakYtelseType());

        var håndterer = oppgaveHendelseHåndtererFactory.lagHåndterer(hendelse);
        håndterer.håndter();
    }

    private FagsakDto hentFagsakDto(BehandlingDto behandlingdto) {
        return behandlingdto.links().stream()
                .filter(rl -> rl.getRel().equals("fagsak"))
                .findFirst()
                .map(ResourceLink::getHref)
                .map(href -> fagsakKlient.get(href, FagsakDto.class))
                .orElseThrow(() -> new IllegalStateException("Fikk ikke hentet FagsakBackendDto"));
    }
}
