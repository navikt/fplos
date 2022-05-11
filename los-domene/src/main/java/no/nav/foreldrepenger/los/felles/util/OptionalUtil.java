package no.nav.foreldrepenger.los.felles.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public final class OptionalUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OptionalUtil.class);

    public OptionalUtil() {
    }

    public static <T> Optional<T> tryOrEmpty(Supplier<T> c) {
        try {
            var result = Optional.of(c.get());
            CustomMetrics.success();
            return result;
        } catch (Exception e) {
            LOG.info("Fikk exception, fortsetter med Optional.empty()", e);
            CustomMetrics.failure();
            return Optional.empty();
        }
    }

}
