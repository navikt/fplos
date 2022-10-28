package no.nav.foreldrepenger.los.web.app;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import no.nav.vedtak.felles.prosesstask.impl.BatchTaskScheduler;
import no.nav.vedtak.felles.prosesstask.impl.TaskManager;

@WebListener
public class TaskManagerStarter implements ServletContextListener {

    @Inject
    TaskManager taskManager;
    @Inject
    BatchTaskScheduler batchTaskScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        taskManager.start();
        batchTaskScheduler.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        taskManager.stop();
        batchTaskScheduler.stop();
    }
}
