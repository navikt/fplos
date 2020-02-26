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
    private EntityManager entityManager;

    private List<KafkaConsumer<?>> consumers = new ArrayList<>();


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        var foreldrepengerConsumer = new KafkaConsumer<>(oppgaveRepository, foreldrepengerConsumerProperties, entityManager, foreldrepengerEventHåndterer);
        consumers.add(foreldrepengerConsumer);
        consumers.forEach(consumers -> consumers.start());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        consumers.forEach(consumers -> consumers.stop());
    }
}
