package no.nav.foreldrepenger.los.felles.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class StreamUtil {
    private StreamUtil() {
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return Optional.ofNullable(list).orElseGet(Collections::emptyList).stream();
    }
}
