package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Fagsystem;
import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.UtvidetBehandlingDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakBackendDto;
import no.nav.fplos.kafkatjenester.ForeldrepengerHendelseHåndterer;
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
    private ForeldrepengerHendelseHåndterer foreldrepengerHendelseHåndterer;

    @Inject
    public SynkroniseringHendelseTask(ForeldrepengerBehandlingKlient behandlingKlient,
                                      ForeldrepengerFagsakKlient fagsakKlient,
                                      ForeldrepengerHendelseHåndterer foreldrepengerHendelseHåndterer) {
        this.behandlingKlient = behandlingKlient;
        this.fagsakKlient = fagsakKlient;
        this.foreldrepengerHendelseHåndterer = foreldrepengerHendelseHåndterer;
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
        hendelse.setSaksnummer(String.valueOf(fagsakDto.getSaksnummer()));
        hendelse.setBehandlendeEnhet(behandlingDto.getBehandlendeEnhetId());
        hendelse.setAktørId(fagsakDto.getAktoerId());
        hendelse.setBehandlingOpprettetTidspunkt(behandlingDto.getOpprettet());
        hendelse.setBehandlingType(behandlingDto.getType());
        hendelse.setYtelseType(fagsakDto.getSakstype());

        foreldrepengerHendelseHåndterer.håndter(hendelse);
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
