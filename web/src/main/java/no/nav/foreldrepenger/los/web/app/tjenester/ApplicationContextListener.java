package no.nav.foreldrepenger.los.web.app.tjenester;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Starter interne applikasjontjenester
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Inject
    private KafkaConsumerStarter kafkaConsumerStarter; //vil ikke fungere med constructor innjection

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        kafkaConsumerStarter.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        kafkaConsumerStarter.destroy();
    }
}
