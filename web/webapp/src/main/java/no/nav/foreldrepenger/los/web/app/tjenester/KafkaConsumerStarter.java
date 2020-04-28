package no.nav.foreldrepenger.los.web.app.tjenester;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.kafkatjenester.ForeldrepengerConsumerProperties;
import no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndterer;
import no.nav.fplos.kafkatjenester.KafkaConsumer;
import no.nav.fplos.kafkatjenester.TilbakekrevingConsumerProperties;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHåndterer;

/**
 * Triggers start of Kafka consum
 */
@ApplicationScoped
public class KafkaConsumerStarter {

    private OppgaveRepository oppgaveRepository;

    private ForeldrepengerConsumerProperties foreldrepengerConsumerProperties;

    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer;

    private TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties;

    private TilbakekrevingEventHåndterer tilbakekrevingEventHåndterer;

    private EntityManager entityManager;

    private List<KafkaConsumer<?>> consumers = new ArrayList<>();

    @Inject
    public KafkaConsumerStarter(OppgaveRepository oppgaveRepository,
                                ForeldrepengerConsumerProperties foreldrepengerConsumerProperties,
                                ForeldrepengerEventHåndterer foreldrepengerEventHåndterer,
                                TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties,
                                TilbakekrevingEventHåndterer tilbakekrevingEventHåndterer,
                                EntityManager entityManager) {
        this.oppgaveRepository = oppgaveRepository;
        this.foreldrepengerConsumerProperties = foreldrepengerConsumerProperties;
        this.foreldrepengerEventHåndterer = foreldrepengerEventHåndterer;
        this.tilbakekrevingConsumerProperties = tilbakekrevingConsumerProperties;
        this.tilbakekrevingEventHåndterer = tilbakekrevingEventHåndterer;
        this.entityManager = entityManager;
    }

    KafkaConsumerStarter() {
        //CDI
    }

    public void start() {
        var foreldrepengerConsumer = new KafkaConsumer<>(oppgaveRepository, foreldrepengerConsumerProperties, entityManager, foreldrepengerEventHåndterer);
        var tilbakekrevingConsumer = new KafkaConsumer<>(oppgaveRepository, tilbakekrevingConsumerProperties, entityManager, tilbakekrevingEventHåndterer);
        destroy();
        consumers.add(foreldrepengerConsumer);
        consumers.add(tilbakekrevingConsumer);
        consumers.forEach(consumer -> consumer.start());
    }

    public void destroy() {
        consumers.forEach(consumer -> consumer.stop());
        consumers = new ArrayList<>();
    }

    public boolean isConsumersRunning() {
        return consumers.stream().allMatch(consumer -> consumer.isRunning());
    }
}
