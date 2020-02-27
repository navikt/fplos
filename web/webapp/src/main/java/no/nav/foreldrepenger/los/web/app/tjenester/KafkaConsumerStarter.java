package no.nav.foreldrepenger.los.web.app.tjenester;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
public class KafkaConsumerStarter implements ServletContextListener {

    @Inject
    private OppgaveRepository oppgaveRepository;

    @Inject
    private ForeldrepengerConsumerProperties foreldrepengerConsumerProperties;

    @Inject
    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer;

    @Inject
    private TilbakekrevingConsumerProperties tilbakekrevingConsumerProperties;

    @Inject
    private TilbakekrevingEventHåndterer tilbakekrevingEventHåndterer;

    @Inject
    private EntityManager entityManager;

    private List<KafkaConsumer<?>> consumers = new ArrayList<>();


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        var foreldrepengerConsumer = new KafkaConsumer<>(oppgaveRepository, foreldrepengerConsumerProperties, entityManager, foreldrepengerEventHåndterer);
        var tilbakekrevingConsumer = new KafkaConsumer<>(oppgaveRepository, tilbakekrevingConsumerProperties, entityManager, tilbakekrevingEventHåndterer);
        consumers.add(foreldrepengerConsumer);
        consumers.add(tilbakekrevingConsumer);
        consumers.forEach(consumers -> consumers.start());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        consumers.forEach(consumers -> consumers.stop());
    }
}
