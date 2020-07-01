package no.nav.foreldrepenger.los.web.app;

import no.nav.vedtak.felles.prosesstask.impl.TaskManager;
import no.nav.vedtak.felles.prosesstask.impl.cron.BatchTaskScheduler;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
