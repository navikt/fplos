package no.nav.foreldrepenger.los.domene.typer;

import java.util.Objects;


public record Saksnummer(String value) {

    public Saksnummer {
        Objects.requireNonNull(value, "value");
    }

    public Long longValue() {
        return Long.valueOf(value);
    }
}
