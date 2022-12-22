package no.nav.foreldrepenger.los.web.app.tjenester;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.BehandlingHendelseConsumer;

/**
 * Triggers start of Kafka consum
 */
@ApplicationScoped
public class KafkaConsumerStarter {

    private BehandlingHendelseConsumer behandlingHendelseConsumer;

    @Inject
    public KafkaConsumerStarter(BehandlingHendelseConsumer behandlingHendelseConsumer) {
        this.behandlingHendelseConsumer = behandlingHendelseConsumer;
    }

    KafkaConsumerStarter() {
        //CDI
    }

    public void start() {
        behandlingHendelseConsumer.start();
    }

    public void destroy() {
        behandlingHendelseConsumer.stop();
    }

    public boolean isKafkaAlive() {
        return behandlingHendelseConsumer.isAlive();
    }
}
