package no.nav.foreldrepenger.los.felles.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static no.nav.foreldrepenger.los.felles.util.CustomMetrics.TRY_OR_EMPTY_FAILURE_COUNTER;
import static no.nav.foreldrepenger.los.felles.util.CustomMetrics.TRY_OR_EMPTY_SUCCESS_COUNTER;

public final class OptionalUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OptionalUtil.class);

    public OptionalUtil() {
    }

    public static <T> Optional<T> tryOrEmpty(Supplier<T> c) {
        try {
            var result = Optional.of(c.get());
            TRY_OR_EMPTY_SUCCESS_COUNTER.increment();
            return result;
        } catch (Exception e) {
            LOG.info("Fikk exception, fortsetter med Optional.empty()", e);
            TRY_OR_EMPTY_FAILURE_COUNTER.increment();
            return Optional.empty();
        }
    }

}
