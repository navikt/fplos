package no.nav.foreldrepenger.los.felles.util;

public class RegexPatterns {

    public static final String ENHETSNUMMER = "\\d{4}";

    private RegexPatterns() {
        throw new IllegalAccessError("Skal ikke instansieres");
    }
}
