package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.Instant;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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

    private static final Set<Hendelse> IGNORER = Set.of(Hendelse.BRUKEROPPGAVE, Hendelse.OPPRETTET, Hendelse.MANGLERSØKNAD, Hendelse.MIGRERING);

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

        var prosessTaskData = ProsessTaskData.forProsessTask(BehandlingHendelseTask.class);
        prosessTaskData.setCallId(behandlingHendelse.getHendelseUuid().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.KILDE, behandlingHendelse.getKildesystem().name());
        prosessTaskData.setProperty(BehandlingHendelseTask.HENDELSE_UUID, behandlingHendelse.getHendelseUuid().toString());
        prosessTaskData.setProperty(BehandlingHendelseTask.HENDELSE_TYPE, behandlingHendelse.getHendelse().name());
        prosessTaskData.setProperty(BehandlingHendelseTask.BEHANDLING_UUID, behandlingHendelse.getBehandlingUuid().toString());
        //setter gruppe og sekvens for rekkefølge
        prosessTaskData.setGruppe(behandlingHendelse.getBehandlingUuid().toString());
        prosessTaskData.setSekvens(String.valueOf(Instant.now().toEpochMilli()));
        taskTjeneste.lagre(prosessTaskData);
    }


}
