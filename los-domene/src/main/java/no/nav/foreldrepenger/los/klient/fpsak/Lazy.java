package no.nav.foreldrepenger.los.klient.fpsak;

import java.util.function.Supplier;

class Lazy<V> {

    private final Supplier<V> supplier;
    private V result;

    public Lazy(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public synchronized V get() {
        if (result == null) {
            result = supplier.get();
        }
        return result;
    }

    public static <V> V get(Lazy<V> o) {
        return o == null ? null : o.get();
    }
}
