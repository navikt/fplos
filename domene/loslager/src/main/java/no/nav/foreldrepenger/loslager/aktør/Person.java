package no.nav.foreldrepenger.loslager.aktør;

import static java.util.Objects.requireNonNull;
import java.util.Locale;

import no.nav.foreldrepenger.domene.typer.AktørId;

public class Person {
    private AktørId aktørId;
    private String navn;
    private Fødselsnummer fødselsnummer;

    public Fødselsnummer getFødselsnummer() {
        return fødselsnummer;
    }

    public String getNavn() {
        return formaterMedStoreOgSmåBokstaver(navn);
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public static class Builder {
        private Person personMal;

        public Builder() {
            personMal = new Person();
        }

        public Builder medAktørId(AktørId aktørId) {
            personMal.aktørId = aktørId;
            return this;
        }

        public Builder medNavn(String navn) {
            personMal.navn = navn;
            return this;
        }

        public Builder medFnr(Fødselsnummer fnr) {
            personMal.fødselsnummer = fnr;
            return this;
        }

        public Person build() {
            requireNonNull(personMal.aktørId, "Mangler aktørId"); //$NON-NLS-1$
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
