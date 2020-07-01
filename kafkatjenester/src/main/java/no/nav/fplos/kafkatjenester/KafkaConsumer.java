package no.nav.fplos.kafkatjenester;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.loslager.hendelse.Hendelse;
import no.nav.foreldrepenger.loslager.repository.HendelseRepository;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.TransactionHandler;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

public final class KafkaConsumer<T extends BehandlingProsessEventDto> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    private KafkaStreams streams;
    private KafkaConsumerProperties properties;
    private EntityManager entityManager;
    private HendelseOppretter<T> hendelseOppretter;
    private ProsessTaskRepository prosessTaskRepository;
    private HendelseRepository hendelseRepository;

    public KafkaConsumer(KafkaConsumerProperties properties,
                         EntityManager entityManager,
                         HendelseOppretter<T> hendelseOppretter,
                         ProsessTaskRepository prosessTaskRepository,
                         HendelseRepository hendelseRepository) {
        this.streams = lagKafkaStreams(properties);
        this.properties = properties;
        this.entityManager = entityManager;
        this.hendelseOppretter = hendelseOppretter;
        this.prosessTaskRepository = prosessTaskRepository;
        this.hendelseRepository = hendelseRepository;
    }

    KafkaConsumer() {
        //CDI
    }

    private KafkaStreams lagKafkaStreams(KafkaConsumerProperties properties) {
        var consumed = Consumed.with(Topology.AutoOffsetReset.EARLIEST);
        var builder = new StreamsBuilder();
        builder.stream(properties.getTopic(), consumed).foreach((header, payload) -> håndterITransaction(payload));

        var topology = builder.build();
        return new KafkaStreams(topology, setupProperties(properties));
    }

    private void håndterITransaction(Object payload) {
        RequestContextHandler.doWithRequestContext(() -> new HåndterEventInTransaction(entityManager, payload).doWork());
    }

    private Properties setupProperties(KafkaConsumerProperties consumerProperties) {
        var properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, consumerProperties.getGroupId());

        var username = consumerProperties.getUsername();
        if (username != null && !username.isEmpty()) {
            properties.setProperty(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            properties.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
            var password = consumerProperties.getPassword();
            if (password != null && !password.isEmpty()) {
                String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
                String jaasCfg = String.format(jaasTemplate, username, password);
                properties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);
            }
        }

        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndFailExceptionHandler.class);
        return properties;
    }

    public void start() {
        addShutdownHooks();
        streams.start();
        LOG.info("Startet konsumering av topic={}, tilstand={}", properties.getTopic(), streams.state());
    }

    public void stop() {
        LOG.info("Starter shutdown av topic={}, tilstand={} med 10 sekunder timeout", properties.getTopic(), streams.state());
        streams.close(Duration.ofSeconds(10));
        LOG.info("Shutdown av topic={}, tilstand={} med 10 sekunder timeout", properties.getTopic(), streams.state());
    }

    private void addShutdownHooks() {
        streams.setStateListener((newState, oldState) -> {
            LOG.info("From state={} to state={}", oldState, newState);

            if (newState == KafkaStreams.State.ERROR) {
                // if the stream has died there is no reason to keep spinning
                LOG.warn("No reason to keep living, closing stream");
                stop();
            }
        });
        streams.setUncaughtExceptionHandler((t, e) -> {
            LOG.error("Caught exception in stream, exiting", e);
            stop();
        });
    }

    public static BehandlingProsessEventDto deserialiser(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.addMixIn(BehandlingProsessEventDto.class, BehandlingProsessEventDtoMixin.class);
        try {
            return mapper.readValue(payload, BehandlingProsessEventDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return streams.state().isRunningOrRebalancing();
    }

    private class HåndterEventInTransaction extends TransactionHandler<Void> {
        private final EntityManager entityManager;
        private final Object payload;

        public HåndterEventInTransaction(EntityManager entityManager, Object payload) {
            this.entityManager = entityManager;
            this.payload = payload;
        }

        protected Void doWork() {
            try {
                super.apply(entityManager);
            } catch (Exception e) {
                LOG.error("Uventet feil", e);
                throw new RuntimeException(e);
            } finally {
                CDI.current().destroy(entityManager);
            }
            return null;
        }

        @Override
        protected Void doWork(EntityManager em) {
            var dto = deserialiser(String.valueOf(payload));
            LOG.debug("Håndterer event {}", dto.getEksternId());
            //TODO callid
            var hendelse = hendelseOppretter.opprett((T) dto);
            hendelseRepository.lagre(hendelse);
            prosessTaskRepository.lagre(opprettTask(hendelse));
            return null;
        }
    }

    private ProsessTaskData opprettTask(Hendelse hendelse) {
        ProsessTaskData prosessTaskData = new ProsessTaskData(HåndterHendelseTask.TASKTYPE);
        prosessTaskData.setProperty(HåndterHendelseTask.HENDELSE_ID, hendelse.getId().toString());
        prosessTaskData.setPrioritet(50);
        //setter gruppe og sekvens for rekkefølge
        prosessTaskData.setGruppe(hendelse.getBehandlingId().toString());
        prosessTaskData.setSekvens(String.valueOf(Instant.now().toEpochMilli()));
        return prosessTaskData;
    }
}
