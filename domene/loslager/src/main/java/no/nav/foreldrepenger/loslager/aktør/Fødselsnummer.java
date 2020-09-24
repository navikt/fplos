package no.nav.foreldrepenger.loslager.aktør;

import javax.validation.constraints.Pattern;


import java.util.Objects;

import static no.nav.foreldrepenger.util.StringUtil.mask;

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

    public static boolean erFødselsnummer(String kandidat) {
        return kandidat.matches("\\d{11}");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + mask(fødselsnummer) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fødselsnummer that = (Fødselsnummer) o;
        return fødselsnummer.equals(that.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fødselsnummer);
    }
}
