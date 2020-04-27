package no.nav.foreldrepenger.loslager;

import java.util.Objects;
import java.util.UUID;

public class BehandlingId implements Comparable<UUID> {

    private UUID value;

    public BehandlingId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static BehandlingId fromUUID(UUID uuid) {
        return new BehandlingId(uuid);
    }

    public static BehandlingId fromString(String string) {
        return fromUUID(UUID.fromString(string));
    }

    /**
     * Tiltenkt testscope
     */
    public static BehandlingId random() {
        return new BehandlingId(UUID.randomUUID());
    }

    public UUID toUUID() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlingId that = (BehandlingId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(UUID uuid) {
        return value.compareTo(uuid);
    }
}
