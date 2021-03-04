package no.nav.foreldrepenger.los.oppgave.risikovurdering;

import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.util.env.Environment;
import org.apache.kafka.common.serialization.Serdes;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RisikoklassifiseringStreamKafkaProperties {
    private static final Environment ENV = Environment.current();
    private final String bootstrapServers;
    private final String username;
    private final String password;
    private final String topic;
    private final String applicationId;


    @Inject
    public RisikoklassifiseringStreamKafkaProperties(@KonfigVerdi("kafka.brokers") String bootstrapServers,
                                                     @KonfigVerdi("systembruker.username") String username,
                                                     @KonfigVerdi("systembruker.password") String password,
                                                     @KonfigVerdi("kafka.kontroll.resultat.topic") String topic) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.username = username;
        this.password = password;
        this.applicationId = applicationId();
    }

    String getBootstrapServers() {
        return bootstrapServers;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getTopic() {
        return topic;
    }

    boolean harSattBrukernavn() {
        return username != null && !username.isEmpty();
    }

    String getConsumerClientId() {
        return "KC-" + topic;
    }

    Class<?> getKeyClass() {
        return Serdes.String().getClass();
    }

    Class<?> getValueClass() {
        return Serdes.String().getClass();
    }

    String getApplicationId() {
        return applicationId;
    }

    private static String applicationId() {
        String prefix = "fplos";
        if (ENV.isProd()) {
            return prefix + "-default";
        }
        return prefix + "-" + ENV.namespace();
    }

}
