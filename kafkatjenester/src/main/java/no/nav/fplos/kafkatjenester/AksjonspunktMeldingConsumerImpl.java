package no.nav.fplos.kafkatjenester;

import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Properties;

@ApplicationScoped
public class AksjonspunktMeldingConsumerImpl implements AksjonspunktMeldingConsumer {
    private static final int TIMEOUT = 10000;
    private KafkaConsumer<String, String> kafkaConsumer;

    public AksjonspunktMeldingConsumerImpl() {
        // for CDI
    }

    @Inject
    public AksjonspunktMeldingConsumerImpl(//@KonfigVerdi("kafka.aksjonspunkthendelse.topic") String topic,
                                           @KonfigVerdi("kafka.tilbakebetaling.topic") String topic, // temp for test
            @KonfigVerdi("kafka.brokers") String bootstrapServers,
            //@KonfigVerdi("kafka.aksjonspunkthendelse.group.id") String groupId,
                                           @KonfigVerdi("kafka.consumer.group.id") String groupId, // temp for test
            @KonfigVerdi("systembruker.username") String username,
            @KonfigVerdi("systembruker.password") String password,
            @KonfigVerdi(value = "disable.ssl", required = false) Boolean disableSsl) {
        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,  groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

        if (disableSsl == null || !disableSsl) {
            setSecurity(username, properties);
        }
        addUserToProperties(username, password, properties);
        this.kafkaConsumer = createConsumer(properties);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    private void setSecurity(String username, Properties properties) {
        if (username != null && !username.isEmpty()) {
            properties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            properties.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
    }

    private void addUserToProperties(@KonfigVerdi("kafka.username") String username, @KonfigVerdi("kafka.password") String password, Properties properties) {
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            String jaasCfg = String.format(jaasTemplate, username, password);
            properties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);
        }
    }

    public ConsumerRecords<String, String> hentConsumerMeldingene() {
        return kafkaConsumer.poll(TIMEOUT);
    }

    public void manualCommitSync() {
        kafkaConsumer.commitSync();
    }

    private KafkaConsumer<String, String> createConsumer(Properties properties) {
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(properties);
    }
}
