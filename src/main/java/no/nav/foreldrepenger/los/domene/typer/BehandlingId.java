package no.nav.foreldrepenger.los.domene.typer;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BehandlingId {

    @Column(name = "behandling_id")
    private UUID value;

    public BehandlingId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public UUID getValue() {
        return value;
    }

    public static BehandlingId fromUUID(UUID uuid) {
        return new BehandlingId(uuid);
    }

    public static BehandlingId fromString(String string) {
        return fromUUID(UUID.fromString(string));
    }

    public BehandlingId() {
        //hiberate
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
        if (this == o) {
            return true;
        }
        return o instanceof BehandlingId that && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
