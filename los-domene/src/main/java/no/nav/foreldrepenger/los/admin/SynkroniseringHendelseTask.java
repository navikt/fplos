package no.nav.foreldrepenger.los.admin;

import no.nav.foreldrepenger.los.admin.dto.FagsakBackendDto;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveHendelseHåndtererFactory;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Hendelse;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerBehandlingKlient;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsakKlient;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.ResourceLink;
import no.nav.foreldrepenger.los.klient.fpsak.dto.behandling.UtvidetBehandlingDto;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
@ProsessTask(SynkroniseringHendelseTask.TASKTYPE)
public class SynkroniseringHendelseTask implements ProsessTaskHandler {

    public static final String TASKTYPE = "synkronisering.hendelse";

    private ForeldrepengerBehandlingKlient behandlingKlient;
    private ForeldrepengerFagsakKlient fagsakKlient;
    private OppgaveHendelseHåndtererFactory oppgaveHendelseHåndtererFactory;

    @Inject
    public SynkroniseringHendelseTask(ForeldrepengerBehandlingKlient behandlingKlient,
                                      ForeldrepengerFagsakKlient fagsakKlient,
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
        String behandlingId = prosessTaskData.getPropertyValue(ProsessTaskData.BEHANDLING_ID);
        var behandlingDto = behandlingKlient.hentUtvidetBehandlingDto(behandlingId);
        var fagsakDto = hentFagsakDto(behandlingDto);

        Hendelse hendelse = new Hendelse();
        hendelse.setFagsystem(Fagsystem.FPSAK);
        hendelse.setBehandlingId(BehandlingId.fromUUID(behandlingDto.getUuid()));
        hendelse.setSaksnummer(fagsakDto.getSaksnummerString());
        hendelse.setBehandlendeEnhet(behandlingDto.getBehandlendeEnhetId());
        hendelse.setAktørId(fagsakDto.getAktoerId());
        hendelse.setBehandlingOpprettetTidspunkt(behandlingDto.getOpprettet());
        hendelse.setBehandlingType(behandlingDto.getType());
        hendelse.setYtelseType(fagsakDto.getSakstype());

        var håndterer = oppgaveHendelseHåndtererFactory.lagHåndterer(hendelse);
        håndterer.håndter();
    }

    private FagsakBackendDto hentFagsakDto(UtvidetBehandlingDto behandlingdto) {
        return behandlingdto.getLinks().stream()
                .filter(rl -> rl.getRel().equals("fagsak-backend"))
                .findFirst()
                .map(ResourceLink::getHref)
                .map(href -> fagsakKlient.get(href, FagsakBackendDto.class))
                .orElseThrow(() -> new IllegalStateException("Fikk ikke hentet FagsakBackendDto"));
    }
}
