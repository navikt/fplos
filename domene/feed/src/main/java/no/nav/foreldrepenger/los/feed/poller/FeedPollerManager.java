package no.nav.foreldrepenger.los.feed.poller;

import no.nav.vedtak.apptjeneste.AppServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Main class handling polling JSON feed.
 */
@ApplicationScoped
public class FeedPollerManager implements AppServiceHandler {

    private static final Logger log = LoggerFactory.getLogger(FeedPollerManager.class);
    private static final long FIXED_POLLING_DELAY = 500L;

    private EntityManager entityManager;

    /**
     * Future for å kunne kansellere polling.
     */
    private Collection<ScheduledFuture<?>> pollingServiceScheduledFuture;
    private Instance<FeedPoller> feedPollers;

    public FeedPollerManager() {
    }

    @Inject
    public FeedPollerManager(EntityManager entityManager, @Any Instance<FeedPoller> feedPollers) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        Objects.requireNonNull(feedPollers, "feedPollers"); //$NON-NLS-1$
        this.entityManager = entityManager;
        this.feedPollers = feedPollers;
    }

    @Override
    public synchronized void start() {
        log.info("Sjekker om lesing fra kafka-køen skal gjennomføres");
        log.info("Lesing fra kafka startes nå");
        startPollerThread();
    }

    @Override
    public synchronized void stop() {
        if (pollingServiceScheduledFuture != null) {
            for (ScheduledFuture<?> scheduledFuture : pollingServiceScheduledFuture) {
                scheduledFuture.cancel(true);
            }
            pollingServiceScheduledFuture = null;
        }
    }

    synchronized void startPollerThread() {
        if (pollingServiceScheduledFuture != null) {
            throw new IllegalStateException("Service allerede startet, stopp først");
        }
        this.pollingServiceScheduledFuture = feedPollers.stream()
                .map(feedPoller -> lagScheduledFuture(feedPoller))
                .collect(Collectors.toList());
    }

    private ScheduledFuture<?> lagScheduledFuture(FeedPoller feedPoller) {
        var threadName = getClass().getSimpleName() + "-" + feedPoller.getName() + "-poller";
        var scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, threadName));
        var poller = new Poller(entityManager, feedPoller);
        var scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(poller, FIXED_POLLING_DELAY, FIXED_POLLING_DELAY, TimeUnit.MILLISECONDS);
        log.info("Laget scheduledfuture poller={}, delayBetweenPollingMillis={}, threadname={}", feedPoller.getName(), FIXED_POLLING_DELAY, threadName);
        return scheduledFuture;
    }
}
