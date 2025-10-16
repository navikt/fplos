package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;

public record Person(Fødselsnummer fødselsnummer, String navn) {

    public Person {
        Objects.requireNonNull(navn);
        Objects.requireNonNull(fødselsnummer);
    }

}
