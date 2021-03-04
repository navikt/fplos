package no.nav.foreldrepenger.los.oppgave.risikovurdering;

import no.nav.vedtak.apptjeneste.AppServiceHandler;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Properties;

@ApplicationScoped
public class RisikoklassifiseringStream implements AppServiceHandler, KafkaIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(RisikoklassifiseringStream.class);

    private String topic;
    private KafkaStreams stream;

    @Inject
    public RisikoklassifiseringStream(RisikoklassifiseringStreamKafkaProperties streamProperties,
                                      RisikoklassifiseringMeldingsHåndterer meldingsHåndterer) {
        this.topic = streamProperties.getTopic();
        this.stream = createKafkaStream(topic, meldingsHåndterer, streamProperties);
    }

    RisikoklassifiseringStream() {
    }

    private static KafkaStreams createKafkaStream(String topic, RisikoklassifiseringMeldingsHåndterer meldingsHåndterer, RisikoklassifiseringStreamKafkaProperties streamProperties) {
        var props = setupProperties(streamProperties);
        Consumed<String, String> stringStringConsumed = Consumed.with(Topology.AutoOffsetReset.EARLIEST);
        var builder = new StreamsBuilder();
        builder.stream(topic, stringStringConsumed)
                .foreach((header, payload) -> meldingsHåndterer.lagreMelding(payload));
        return new KafkaStreams(builder.build(), props);
    }

    private static Properties setupProperties(RisikoklassifiseringStreamKafkaProperties streamProperties) {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamProperties.getApplicationId() + "-" + streamProperties.getConsumerClientId());
        props.put(StreamsConfig.CLIENT_ID_CONFIG, streamProperties.getConsumerClientId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, streamProperties.getBootstrapServers());

        // Sikkerhet
        if (streamProperties.harSattBrukernavn()) {
            LOG.info("Using user name {} to authenticate against Kafka brokers ", streamProperties.getUsername());
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(jaasTemplate, streamProperties.getUsername(), streamProperties.getPassword()));
        }

        // Serde
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, streamProperties.getKeyClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, streamProperties.getValueClass());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndFailExceptionHandler.class);

        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "at_least_once");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "100000");

        return props;
    }

    @Override
    public void start() {
        addShutdownHooks();

        stream.start();
        LOG.info("Starter konsumering av topic={}, tilstand={}", topic, stream.state());
    }

    @Override
    public void stop() {
        LOG.info("Starter shutdown av topic={}, tilstand={} med 10 sekunder timeout", topic, stream.state());
        stream.close(Duration.ofSeconds(10));
        LOG.info("Shutdown av topic={}, tilstand={} med 10 sekunder timeout", topic, stream.state());
    }

    @Override
    public boolean isAlive() {
        return stream != null && stream.state().isRunningOrRebalancing();
    }

    public KafkaStreams.State getTilstand() {
        return stream.state();
    }

    public String getTopic() {
        return topic;
    }

    private void addShutdownHooks() {
        stream.setStateListener((newState, oldState) -> {
            LOG.info("From state={} to state={}", oldState, newState);

            if (newState == KafkaStreams.State.ERROR) {
                // if the stream has died there is no reason to keep spinning
                LOG.warn("{} - No reason to keep living, closing stream", getTopic());
                stop();
            }
        });
        stream.setUncaughtExceptionHandler((t, e) -> {
            LOG.error("{} - Caught exception in stream, exiting", getTopic(), e);
            stop();
        });
    }

}

