package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.FpsakOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingHendelseHåndterer;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.hendelser.behandling.Kildesystem;

@Dependent
@ProsessTask(value = "håndter.behandlinghendelse", firstDelay = 10, thenDelay = 10)
public class BehandlingHendelseTask implements ProsessTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseTask.class);

    public static final String HENDELSE_UUID = "hendelseUuid";
    public static final String BEHANDLING_UUID = "behandlingUuid";
    public static final String HENDELSE_TYPE = "hendelseType";
    public static final String KILDE = "kildesystem";

    private final BehandlingKlient fpsakKlient;
    private final BehandlingKlient fptilbakeKlient;

    private TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer;
    private FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer;

    @Inject
    public BehandlingHendelseTask(FpsakBehandlingKlient fpsakKlient,
                                  FptilbakeBehandlingKlient fptilbakeKlient,
                                  TilbakekrevingHendelseHåndterer tilbakekrevingHendelseHåndterer,
                                  FpsakOppgaveHendelseHåndterer fpsakOppgaveHendelseHåndterer) {
        this.fpsakKlient = fpsakKlient;
        this.fptilbakeKlient = fptilbakeKlient;
        this.tilbakekrevingHendelseHåndterer = tilbakekrevingHendelseHåndterer;
        this.fpsakOppgaveHendelseHåndterer = fpsakOppgaveHendelseHåndterer;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var hendelseUuidString = prosessTaskData.getPropertyValue(HENDELSE_UUID);
        var behandlingUuid = UUID.fromString(prosessTaskData.getPropertyValue(BEHANDLING_UUID));
        var kilde = Kildesystem.valueOf(prosessTaskData.getPropertyValue(KILDE));

        if (Kildesystem.FPSAK.equals(kilde)) {
            LOG.info("FPLOS FPSAK hendelse {} behandling {}", hendelseUuidString, behandlingUuid);
            fpsakOppgaveHendelseHåndterer.håndterBehandling(fpsakKlient.hentLosBehandlingDto(behandlingUuid));
        } else {
            LOG.info("FPLOS FPTILBAKE hendelse {} behandling {}", hendelseUuidString, behandlingUuid);
            var behandling = fptilbakeKlient.hentLosBehandlingDto(behandlingUuid);
            var egenskaper = fpsakKlient.hentLosFagsakEgenskaperDto(new Saksnummer(behandling.saksnummer()));
            tilbakekrevingHendelseHåndterer.håndterBehandling(behandling, egenskaper);
        }

    }
}
