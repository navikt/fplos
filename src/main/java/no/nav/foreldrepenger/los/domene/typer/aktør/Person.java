package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;

import no.nav.foreldrepenger.los.felles.util.StringUtils;

public record Person(Fødselsnummer fødselsnummer, String navn) {

    public Person {
        Objects.requireNonNull(navn);
        Objects.requireNonNull(fødselsnummer);
    }

    public static Person personMedFormattertNavn(Fødselsnummer fnr, String navn) {
        return new Person(fnr, StringUtils.formaterMedStoreOgSmåBokstaver(navn));
    }

}
