package no.nav.foreldrepenger.los.domene.typer.aktør;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;

public class Person {

    private String navn;
    private Fødselsnummer fødselsnummer;

    public Fødselsnummer getFødselsnummer() {
        return fødselsnummer;
    }

    public String getNavn() {
        return navn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var person = (Person) o;
        return navn.equals(person.navn) && fødselsnummer.equals(person.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, fødselsnummer);
    }

    public static class Builder {

        private final Person personMal;

        public Builder() {
            personMal = new Person();
        }

        public Builder medNavn(String navn) {
            personMal.navn = formaterMedStoreOgSmåBokstaver(navn);
            return this;
        }

        public Builder medFnr(Fødselsnummer fnr) {
            personMal.fødselsnummer = fnr;
            return this;
        }

        public Person build() {
            requireNonNull(personMal.fødselsnummer, "Mangler ident"); //$NON-NLS-1$
            requireNonNull(personMal.navn, "Mangler navn"); //$NON-NLS-1$
            return personMal;
        }

    }

    private static String formaterMedStoreOgSmåBokstaver(String tekst) {
        if (tekst == null || (tekst = tekst.trim()).isEmpty()) { // NOSONAR
            return null;
        }
        var skilletegnPattern = "(\\s|[()\\-_.,/])";
        var tegn = tekst.toLowerCase(Locale.getDefault()).toCharArray();
        var nesteSkalHaStorBokstav = true;
        for (var i = 0; i < tegn.length; i++) {
            var erSkilletegn = String.valueOf(tegn[i]).matches(skilletegnPattern);
            if (!erSkilletegn && nesteSkalHaStorBokstav) {
                tegn[i] = Character.toTitleCase(tegn[i]);
            }
            nesteSkalHaStorBokstav = erSkilletegn;
        }
        return new String(tegn);
    }
}
