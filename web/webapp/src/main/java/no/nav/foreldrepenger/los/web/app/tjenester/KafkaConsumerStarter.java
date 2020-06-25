package no.nav.foreldrepenger.los.web.app.tjenester;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.loslager.repository.HendelseRepository;
import no.nav.fplos.kafkatjenester.ForeldrepengerConsumerProperties;
import no.nav.fplos.kafkatjenester.ForeldrepengerHendelseOppretter;
import no.nav.fplos.kafkatjenester.KafkaConsumer;
import no.nav.fplos.kafkatjenester.TilbakekrevingConsumerProperties;
import no.nav.fplos.kafkatjenester.TilbakekrevingHendelseOppretter;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskRepository;

/**
 * Triggers start of Kafka consum
 */
@ApplicationScoped
public class KafkaConsumerStarter {

    private ProsessTaskRepository prosessTaskRepository;

    private HendelseRepository hendelseRepository;

    private ForeldrepengerConsumerProperties foreldrepengerConsumerProperties;

    private ForeldrepengerHendelseOppretter foreldrepengerEventHåndterer;

    private TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties;

    private TilbakekrevingHendelseOppretter tilbakekrevingEventHåndterer;

    private EntityManager entityManager;

    private List<KafkaConsumer<?>> consumers = new ArrayList<>();

    @Inject
    public KafkaConsumerStarter(HendelseRepository hendelseRepository,
                                ProsessTaskRepository prosessTaskRepository,
                                ForeldrepengerConsumerProperties foreldrepengerConsumerProperties,
                                ForeldrepengerHendelseOppretter foreldrepengerEventHåndterer,
                                TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties,
                                TilbakekrevingHendelseOppretter tilbakekrevingEventHåndterer,
                                EntityManager entityManager) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskRepository = prosessTaskRepository;
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
        var foreldrepengerConsumer = new KafkaConsumer<>(foreldrepengerConsumerProperties, entityManager,
                foreldrepengerEventHåndterer, prosessTaskRepository, hendelseRepository);
        var tilbakekrevingConsumer = new KafkaConsumer<>(tilbakekrevingConsumerProperties, entityManager,
                tilbakekrevingEventHåndterer, prosessTaskRepository, hendelseRepository);
        destroy();
        consumers.add(foreldrepengerConsumer);
        consumers.add(tilbakekrevingConsumer);
        consumers.forEach(consumer -> consumer.start());
    }

    public void destroy() {
        consumers.forEach(consumer -> consumer.stop());
    }

    public boolean isConsumersRunning() {
        return consumers.stream().allMatch(consumer -> consumer.isRunning());
    }
}
