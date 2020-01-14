package no.nav.foreldrepenger.los.feed.kafka;

import no.nav.foreldrepenger.los.feed.poller.FeedPoller;
import no.nav.fplos.kafkatjenester.KafkaReader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KafkaFeedPoller implements FeedPoller {

    public static final String FEED_NAME = "KAFKA_FORELDREPENGER_EVENT_KØ";

    private KafkaReader kafaReader;

    public KafkaFeedPoller() {
    }

    @Inject
    public KafkaFeedPoller(KafkaReader kafaReader) {
        this.kafaReader = kafaReader;
    }

    @Override
    public String getName() {
        return FEED_NAME;
    }

    @Override
    public void poll() {
        kafaReader.hentOgLagreMeldingene();
    }
}
