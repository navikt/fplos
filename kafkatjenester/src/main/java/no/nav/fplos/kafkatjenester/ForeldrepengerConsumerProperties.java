package no.nav.fplos.kafkatjenester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class ForeldrepengerConsumerProperties implements KafkaConsumerProperties {
    private String topic;
    private String bootstrapServers;
    private String groupId;
    private String username;
    private String password;

    @Inject
    public ForeldrepengerConsumerProperties(@KonfigVerdi("kafka.aksjonspunkthendelse.topic") String topic,
                                            @KonfigVerdi("kafka.brokers") String bootstrapServers,
                                            @KonfigVerdi("kafka.aksjonspunkthendelse.group.id") String groupId,
                                            @KonfigVerdi("systembruker.username") String username,
                                            @KonfigVerdi("systembruker.password") String password) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.username = username;
        this.password = password;
    }

    ForeldrepengerConsumerProperties() {
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
}
