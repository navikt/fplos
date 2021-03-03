package no.nav.foreldrepenger.los.domene.typer;

import javax.validation.constraints.Pattern;
import java.util.Objects;


public class Saksnummer {
    @Pattern(regexp = "^(-?[1-9]|[a-z0])[a-z0-9_:-]*$")
    private final String saksnummer;

    public Saksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer");
        this.saksnummer = saksnummer;
    }

    public String getVerdi() {
        return saksnummer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        Saksnummer other = (Saksnummer) obj;
        return Objects.equals(saksnummer, other.saksnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + saksnummer + ">";
    }
}
