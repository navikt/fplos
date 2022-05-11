package no.nav.foreldrepenger.los.felles.util;


import io.micrometer.core.instrument.Counter;

import static no.nav.vedtak.log.metrics.MetricsUtil.REGISTRY;

public final class CustomMetrics {

    private static final Counter FAILURE_COUNTER = counter("failure");
    private static final Counter SUCCESS_COUNTER = counter("success");

    private CustomMetrics() {
    }

    public static void success() {
        SUCCESS_COUNTER.increment();
    }

    public static void failure() {
        FAILURE_COUNTER.increment();
    }

    private static Counter counter(String status) {
        return Counter.builder("foreldrepenger.fplos.besteffort")
                .tag("status", status)
                .description("TryOrEmpty result")
                .register(REGISTRY);
    }

}
