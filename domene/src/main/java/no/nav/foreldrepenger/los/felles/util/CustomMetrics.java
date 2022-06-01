package no.nav.foreldrepenger.los.felles.util;


import io.micrometer.core.instrument.Counter;

import static no.nav.vedtak.log.metrics.MetricsUtil.REGISTRY;

public final class CustomMetrics {

    public static final Counter TRY_OR_EMPTY_FAILURE_COUNTER = counter("failure");
    public static final Counter TRY_OR_EMPTY_SUCCESS_COUNTER = counter("success");

    private CustomMetrics() {
    }

    private static Counter counter(String status) {
        return Counter.builder("foreldrepenger.fplos.tryorempty")
                .tag("status", status)
                .description("TryOrEmpty best effort result")
                .register(REGISTRY);
    }

}
