package no.nav.foreldrepenger.los.felles.util;

import io.micrometer.core.instrument.Counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static no.nav.vedtak.log.metrics.MetricsUtil.REGISTRY;

public final class OptionalUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OptionalUtil.class);
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    public OptionalUtil() {
    }

    public static <T> Optional<T> tryOrEmpty(Supplier<T> supplier, String identifikator) {
        try {
            var result = Optional.of(supplier.get());
            counter(SUCCESS, identifikator).increment();
            return result;
        } catch (Exception e) {
            LOG.info("Fikk exception, fortsetter med Optional.empty() ({})", identifikator, e);
            counter(FAILURE, identifikator).increment();
            return Optional.empty();
        }

    }

    private static Counter counter(String status, String identifikator) {
        return Counter.builder("foreldrepenger.fplos.tryorempty")
                .tag("status", status)
                .tag("identifikator", identifikator)
                .description("TryOrEmpty best effort result")
                .register(REGISTRY);
    }

}
