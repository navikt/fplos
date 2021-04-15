package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;

public record Fødselsnummer(String value) {

    public Fødselsnummer {
        Objects.requireNonNull(value, "fødselsnummer");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + "***********" + "]";
    }
}
