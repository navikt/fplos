package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;

import javax.validation.constraints.Pattern;

public class Fødselsnummer {
    @Pattern(regexp = "\\d{11}")
    private final String fødselsnummer;

    public Fødselsnummer(String fødselsnummer) {
        Objects.requireNonNull(fødselsnummer, "fødselsnummer");
        this.fødselsnummer = fødselsnummer;
    }

    public String asValue() {
        return fødselsnummer;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + "***********" + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (Fødselsnummer) o;
        return fødselsnummer.equals(that.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fødselsnummer);
    }
}
