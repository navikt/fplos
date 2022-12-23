package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDateTime;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;
import no.nav.vedtak.hendelser.behandling.BehandlingHendelse;
import no.nav.vedtak.hendelser.behandling.Hendelse;
import no.nav.vedtak.hendelser.behandling.v1.BehandlingHendelseV1;
import no.nav.vedtak.log.util.LoggerUtils;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

@ApplicationScoped
@ActivateRequestContext
@Transactional
public class BehandlingHendelseHåndterer {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseHåndterer.class);

    private static final Set<Hendelse> IGNORER = Set.of(Hendelse.BRUKEROPPGAVE, Hendelse.OPPRETTET, Hendelse.MANGLERSØKNAD);

    private ProsessTaskTjeneste taskTjeneste;
    private MottattHendelseRepository hendelseRepository;

    public BehandlingHendelseHåndterer() {
    }

    @Inject
    public BehandlingHendelseHåndterer(ProsessTaskTjeneste taskTjeneste, MottattHendelseRepository hendelseRepository) {
        this.taskTjeneste = taskTjeneste;
        this.hendelseRepository = hendelseRepository;
    }

    void handleMessage(String key, String payload) {
        // enhver exception ut fra denne metoden medfører at tråden som leser fra kafka gir opp og dør på seg.
        try {
            var behandlingHendelse = DefaultJsonMapper.fromJson(payload, BehandlingHendelse.class);
            if (behandlingHendelse != null && !IGNORER.contains(behandlingHendelse.getHendelse())) {
                handleMessageIntern((BehandlingHendelseV1) behandlingHendelse);
            }
        } catch (VLException e) {
            LOG.warn("FP-328773 Vedtatt-Ytelse Feil under parsing av vedtak. key={} payload={}", key, payload, e);
        } catch (Exception e) {
            LOG.warn("Vedtatt-Ytelse exception ved håndtering av vedtaksmelding, ignorerer key={}", LoggerUtils.removeLineBreaks(payload), e);
        }
    }

    void handleMessageIntern(BehandlingHendelseV1 behandlingHendelse) {
        var hendelseId = behandlingHendelse.getKildesystem().name() + behandlingHendelse.getHendelseUuid().toString();
        if (!hendelseRepository.hendelseErNy(hendelseId)) {
            LOG.info("FPLOS Mottatt behandlinghendelse på nytt hendelse={}", hendelseId);
            return;
        }
        hendelseRepository.registrerMottattHendelse(hendelseId);

        LOG.info("Mottatt hendelse med id {} kilde {} behandling {} hendelse {}", behandlingHendelse.getHendelseUuid(),
                behandlingHendelse.getKildesystem(), behandlingHendelse.getBehandlingUuid(), behandlingHendelse.getHendelse());

        // TODO: Vurder å sende med type videre. Bør gjennomgå fptilbake-typer først
        var prosessTaskData = ProsessTaskData.forProsessTask(BehandlingHendelseTask.class);
        prosessTaskData.setNesteKjøringEtter(LocalDateTime.now().plusSeconds(10));
        prosessTaskData.setCallId(behandlingHendelse.getHendelseUuid().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.HENDELSE_UUID, behandlingHendelse.getHendelseUuid().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.BEHANDLING_UUID, behandlingHendelse.getBehandlingUuid().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.KILDE, behandlingHendelse.getKildesystem().name());
        taskTjeneste.lagre(prosessTaskData);

    }

}
