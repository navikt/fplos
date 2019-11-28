package no.nav.fplos.batch;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class UUIDSync {
    @Scheduled(cron = "0 15 06 * * ?")
    public void scheduleFixedDelayTask() {
        oppdaterUUID();
    }

    private void oppdaterUUID() {

    }
}
