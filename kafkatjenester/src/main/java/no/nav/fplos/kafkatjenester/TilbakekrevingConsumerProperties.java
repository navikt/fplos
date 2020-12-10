package no.nav.fplos.kafkatjenester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class TilbakekrevingConsumerProperties implements KafkaConsumerProperties {
    private String topic;
    private String bootstrapServers;
    private String offsetResetPolicy;
    private String groupId;
    private String username;
    private String password;

    @Inject
    public TilbakekrevingConsumerProperties(@KonfigVerdi("kafka.tilbakebetaling.topic") String topic,
                                            @KonfigVerdi("kafka.brokers") String bootstrapServers,
                                            @KonfigVerdi(value = "kafka.auto.offset.reset", defaultVerdi = "none") String offsetResetPolicy,
                                            @KonfigVerdi("kafka.tilbakebetaling.group.id") String groupId,
                                            @KonfigVerdi("systembruker.username") String username,
                                            @KonfigVerdi("systembruker.password") String password) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.offsetResetPolicy = offsetResetPolicy;
        this.groupId = groupId;
        this.username = username;
        this.password = password;
    }

    TilbakekrevingConsumerProperties() {
        //CDI
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getOffsetResetPolicy() {
        return offsetResetPolicy;
    }
}
