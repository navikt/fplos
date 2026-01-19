package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.kafka.KafkaMessageHandler;
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
public class BehandlingHendelseHåndterer implements KafkaMessageHandler.KafkaStringMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseHåndterer.class);
    private static final String GROUP_ID = "fplos-behandling"; // Hold konstant pga offset commit !!
    private static final Set<Hendelse> RELEVENATE_TYPER = Set.of(Hendelse.AVSLUTTET, Hendelse.PAPIRSØKNAD, Hendelse.AKSJONSPUNKT,
        Hendelse.VENTETILSTAND, Hendelse.ENHET);

    private ProsessTaskTjeneste taskTjeneste;
    private MottattHendelseRepository hendelseRepository;

    private String topicName;

    public BehandlingHendelseHåndterer() {
    }

    @Inject
    public BehandlingHendelseHåndterer(ProsessTaskTjeneste taskTjeneste, MottattHendelseRepository hendelseRepository,
                                       @KonfigVerdi(value = "kafka.behandlinghendelse.topic", defaultVerdi = "teamforeldrepenger.behandling-hendelse-v1") String topicName) {
        this.taskTjeneste = taskTjeneste;
        this.hendelseRepository = hendelseRepository;
        this.topicName = topicName;
    }

    @Override
    public void handleRecord(String key, String value) {
        // enhver exception ut fra denne metoden medfører at tråden som leser fra kafka gir opp og dør på seg.
        try {
            var behandlingHendelse = DefaultJsonMapper.fromJson(value, BehandlingHendelse.class);
            if (behandlingHendelse != null && RELEVENATE_TYPER.contains(behandlingHendelse.getHendelse())) {
                handleMessageIntern((BehandlingHendelseV1) behandlingHendelse);
            }
        } catch (VLException e) {
            LOG.warn("FP-328773 Vedtatt-Ytelse Feil under parsing av vedtak. key={} payload={}", key, value, e);
        } catch (Exception e) {
            LOG.warn("Vedtatt-Ytelse exception ved håndtering av vedtaksmelding, ignorerer key={}", LoggerUtils.removeLineBreaks(value), e);
        }
    }

    @Override
    public String topic() {
        return topicName;
    }

    @Override
    public String groupId() {
        return GROUP_ID; // Keep stable (or it will read from autoOffsetReset()
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
        Optional.ofNullable(behandlingHendelse.getSaksnummer()).ifPresent(prosessTaskData::setSaksnummer);
        prosessTaskData.setBehandlingUUid(behandlingHendelse.getBehandlingUuid());
        prosessTaskData.setProperty(BehandlingHendelseTask.KILDE, behandlingHendelse.getKildesystem().name());
        //setter gruppe og sekvens for rekkefølge
        prosessTaskData.setGruppe(behandlingHendelse.getBehandlingUuid().toString());
        prosessTaskData.setSekvens(String.valueOf(Instant.now().toEpochMilli()));
        taskTjeneste.lagre(prosessTaskData);
    }


}
