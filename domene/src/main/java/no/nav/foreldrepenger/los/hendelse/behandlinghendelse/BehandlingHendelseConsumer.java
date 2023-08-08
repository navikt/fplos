package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.felles.integrasjon.kafka.KafkaProperties;
import no.nav.vedtak.log.metrics.Controllable;
import no.nav.vedtak.log.metrics.LiveAndReadinessAware;

@ApplicationScoped
public class BehandlingHendelseConsumer implements LiveAndReadinessAware, Controllable {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingHendelseConsumer.class);
    private static final Environment ENV = Environment.current();

    private static final String PROD_APP_ID = "fplos-behandling"; // Hold konstant pga offset commit !!
    private static final int HANDLE_MESSAGE_INTERVAL_MILLIS = 20;

    private String topicName;
    private KafkaStreams stream;

    public BehandlingHendelseConsumer() {
    }

    @Inject
    public BehandlingHendelseConsumer(@KonfigVerdi(value = "kafka.behandlinghendelse.topic", defaultVerdi = "teamforeldrepenger.behandling-hendelse-v1") String topicName,
                                      BehandlingHendelseHåndterer behandlingHendelseHåndterer) {
        this.topicName = topicName;

        final Consumed<String, String> consumed = Consumed.with(Topology.AutoOffsetReset.EARLIEST);

        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream(topicName, consumed).foreach((key, payload) -> {
            behandlingHendelseHåndterer.handleMessage(key, payload);
            sleep();
        });

        this.stream = new KafkaStreams(builder.build(), KafkaProperties.forStreamsStringValue(getApplicationId()));
    }

    private static void sleep() {
        try {
            Thread.sleep(HANDLE_MESSAGE_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            LOG.warn("Interrupt!", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        addShutdownHooks();
        stream.start();
        LOG.info("Starter konsumering av topic={}, tilstand={}", getTopicName(), stream.state());
    }

    @Override
    public void stop() {
        LOG.info("Starter shutdown av topic={}, tilstand={} med 15 sekunder timeout", getTopicName(), stream.state());
        stream.close(Duration.ofSeconds(15));
        LOG.info("Shutdown av topic={}, tilstand={} med 15 sekunder timeout", getTopicName(), stream.state());
    }

    @Override
    public boolean isAlive() {
        return (stream != null && stream.state().isRunningOrRebalancing());
    }

    @Override
    public boolean isReady() {
        return isAlive();
    }

    private void addShutdownHooks() {
        stream.setStateListener((newState, oldState) -> {
            LOG.info("{} :: From state={} to state={}", getTopicName(), oldState, newState);

            if (newState == KafkaStreams.State.ERROR) {
                // if the stream has died there is no reason to keep spinning
                LOG.warn("{} :: No reason to keep living, closing stream", getTopicName());
                stop();
            }
        });
        stream.setUncaughtExceptionHandler(ex -> {
            LOG.error("{} :: Caught exception in stream, exiting", getTopicName(), ex);
            return SHUTDOWN_CLIENT;
        });
    }

    private String getTopicName() {
        return topicName;
    }

    private static String getApplicationId() {
        if (!ENV.isProd()) {
            return PROD_APP_ID + (ENV.isDev() ? "-dev" : "-vtp");
        }
        return PROD_APP_ID;
    }
}
