package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Beløp(@JsonProperty("verdi") BigDecimal value) implements Comparable<Beløp> {

    public static final Beløp ZERO = new Beløp(BigDecimal.ZERO);

    public Beløp {
        Objects.requireNonNull(value, "Trenger value");
    }

    @Override
    public int compareTo(Beløp annetBeløp) {
        return value.compareTo(annetBeløp.value);
    }
}
