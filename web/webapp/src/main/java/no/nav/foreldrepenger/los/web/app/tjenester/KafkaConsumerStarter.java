package no.nav.foreldrepenger.los.web.app.tjenester;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.ForeldrepengerConsumerProperties;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.ForeldrepengerHendelseOppretter;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.HendelseRepository;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.KafkaConsumer;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.TilbakekrevingConsumerProperties;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.TilbakekrevingHendelseOppretter;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTjeneste;

/**
 * Triggers start of Kafka consum
 */
@ApplicationScoped
public class KafkaConsumerStarter {

    private ProsessTaskTjeneste prosessTaskTjeneste;

    private HendelseRepository hendelseRepository;

    private ForeldrepengerConsumerProperties foreldrepengerConsumerProperties;

    private ForeldrepengerHendelseOppretter foreldrepengerEventHåndterer;

    private TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties;

    private TilbakekrevingHendelseOppretter tilbakekrevingEventHåndterer;

    private EntityManager entityManager;

    private List<KafkaConsumer<?>> consumers = new ArrayList<>();

    @Inject
    public KafkaConsumerStarter(HendelseRepository hendelseRepository,
                                ProsessTaskTjeneste prosessTaskTjeneste,
                                ForeldrepengerConsumerProperties foreldrepengerConsumerProperties,
                                ForeldrepengerHendelseOppretter foreldrepengerEventHåndterer,
                                TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties,
                                TilbakekrevingHendelseOppretter tilbakekrevingEventHåndterer,
                                EntityManager entityManager) {
        this.hendelseRepository = hendelseRepository;
        this.prosessTaskTjeneste = prosessTaskTjeneste;
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
                foreldrepengerEventHåndterer, prosessTaskTjeneste, hendelseRepository);
        var tilbakekrevingConsumer = new KafkaConsumer<>(tilbakekrevingConsumerProperties, entityManager,
                tilbakekrevingEventHåndterer, prosessTaskTjeneste, hendelseRepository);
        destroy();
        consumers.add(foreldrepengerConsumer);
        consumers.add(tilbakekrevingConsumer);
        consumers.forEach(KafkaConsumer::start);
    }

    public void destroy() {
        consumers.forEach(KafkaConsumer::stop);
    }

    public boolean isKafkaAlive() {
        return consumers.stream().allMatch(KafkaConsumer::isAlive);
    }
}
