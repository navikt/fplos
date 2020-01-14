package no.nav.fplos.kafkatjenester;

import no.nav.familie.topic.Environment;
import no.nav.familie.topic.Topic;
import no.nav.familie.topic.TopicManifest;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Properties;

@ApplicationScoped
public class AksjonspunktMeldingConsumerImpl implements AksjonspunktMeldingConsumer {

    private KafkaConsumer<String, String> kafkaConsumer;
    private static final int TIMEOUT = 10000;
    private static final Topic topic = TopicManifest.AKSJONSPUNKT_HENDELSE;
    // for proxy
    public AksjonspunktMeldingConsumerImpl() {}

    @Inject
    public AksjonspunktMeldingConsumerImpl(
            @KonfigVerdi("bootstrap.servers") String bootstrapServers,
            @KonfigVerdi("systembruker.username") String username,
            @KonfigVerdi("systembruker.password") String password,
            @KonfigVerdi(value = "disable.ssl", required = false) Boolean disableSsl,
            @KonfigVerdi(value = "fasit.environment.name", required = false) String environmentName
    ) {

        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic.getConsumerClientId());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");

        if (disableSsl == null || !disableSsl) {
            setSecurity(username, properties);
        }
        if (environmentName == null) {
            environmentName = Environment.local.name();
        }
        addUserToProperties(username, password, properties);

        this.kafkaConsumer = createConsumer(properties);

        String topicName = topic.getTopicWithEnv(environmentName);
        kafkaConsumer.subscribe(Collections.singletonList(topicName));
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
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, topic.getSerdeKey().deserializer().getClass().getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, topic.getSerdeValue().deserializer().getClass().getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(properties);
    }
}
