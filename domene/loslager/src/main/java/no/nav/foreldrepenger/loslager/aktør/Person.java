package no.nav.foreldrepenger.loslager.aktør;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class Person {

    private String navn;
    private Fødselsnummer fødselsnummer;

    private NavBrukerKjønn kjønn;
    private String diskresjonskode;
    private LocalDate fødselsdato;
    private LocalDate dødsdato;


    public Fødselsnummer getFødselsnummer() {
        return fødselsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public NavBrukerKjønn getKjønn() {
        return kjønn;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return navn.equals(person.navn) &&
                fødselsnummer.equals(person.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, fødselsnummer);
    }

    public static class Builder {

        private Person personMal;
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

        public Builder medKjønn(NavBrukerKjønn kjønn) {
            personMal.kjønn = kjønn;
            return this;
        }

        public Builder medDiskresjonskode(String kode) {
            personMal.diskresjonskode = kode;
            return this;
        }

        public Builder medFødselsdato(LocalDate dato) {
            personMal.fødselsdato = dato;
            return this;
        }

        public Builder medDødsdato(LocalDate dato) {
            personMal.dødsdato = dato;
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
        String skilletegnPattern = "(\\s|[()\\-_.,/])";
        char[] tegn = tekst.toLowerCase(Locale.getDefault()).toCharArray();
        boolean nesteSkalHaStorBokstav = true;
        for (int i = 0; i < tegn.length; i++) {
            boolean erSkilletegn = String.valueOf(tegn[i]).matches(skilletegnPattern);
            if (!erSkilletegn && nesteSkalHaStorBokstav) {
                tegn[i] = Character.toTitleCase(tegn[i]);
            }
            nesteSkalHaStorBokstav = erSkilletegn;
        }
        return new String(tegn);
    }
}
