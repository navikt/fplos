package no.nav.foreldrepenger.los.web.app.tjenester;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.HendelseRepository;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.ForeldrepengerConsumerProperties;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.ForeldrepengerHendelseOppretter;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.KafkaConsumer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.TilbakekrevingConsumerProperties;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.TilbakekrevingHendelseOppretter;
import no.nav.foreldrepenger.los.oppgave.risikovurdering.RisikoklassifiseringStream;
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
    private final List<KafkaConsumer<?>> consumers = new ArrayList<>();
    private RisikoklassifiseringStream risikoConsumer;


    @Inject
    public KafkaConsumerStarter(HendelseRepository hendelseRepository,
                                ProsessTaskRepository prosessTaskRepository,
                                ForeldrepengerConsumerProperties foreldrepengerConsumerProperties,
                                ForeldrepengerHendelseOppretter foreldrepengerEventHåndterer,
                                TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties,
                                TilbakekrevingHendelseOppretter tilbakekrevingEventHåndterer,
                                EntityManager entityManager,
                                RisikoklassifiseringStream risikoConsumer) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskRepository = prosessTaskRepository;
        this.foreldrepengerConsumerProperties = foreldrepengerConsumerProperties;
        this.foreldrepengerEventHåndterer = foreldrepengerEventHåndterer;
        this.tilbakekrevingConsumerProperties = tilbakekrevingConsumerProperties;
        this.tilbakekrevingEventHåndterer = tilbakekrevingEventHåndterer;
        this.entityManager = entityManager;
        this.risikoConsumer = risikoConsumer;
    }

    KafkaConsumerStarter() {
        //CDI
    }

    public void start() {
        var foreldrepengerConsumer = new KafkaConsumer<>(foreldrepengerConsumerProperties, entityManager,
                foreldrepengerEventHåndterer, prosessTaskRepository, hendelseRepository);
        var tilbakekrevingConsumer = new KafkaConsumer<>(tilbakekrevingConsumerProperties, entityManager,
                tilbakekrevingEventHåndterer, prosessTaskRepository, hendelseRepository);
        risikoConsumer.start();
        consumers.add(foreldrepengerConsumer);
        consumers.add(tilbakekrevingConsumer);
        consumers.forEach(KafkaConsumer::start);
    }

    public void destroy() {
        consumers.forEach(KafkaConsumer::stop);
        risikoConsumer.stop();
    }

    public boolean isKafkaAlive() {
        return consumers.stream().allMatch(KafkaConsumer::isAlive) && risikoConsumer.isAlive();
    }
}
