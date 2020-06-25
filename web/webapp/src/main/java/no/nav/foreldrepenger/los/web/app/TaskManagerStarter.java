package no.nav.foreldrepenger.los.web.app;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import no.nav.vedtak.felles.prosesstask.impl.TaskManager;

@WebListener
public class TaskManagerStarter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // kan gjøre programmatisk lookup siden TaskManager er ApplicationScoped (en per applikasjoninstans)
        var taskManager = CDI.current().select(TaskManager.class).get();
        taskManager.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        var taskManager = CDI.current().select(TaskManager.class).get();
        taskManager.stop();
    }
}
