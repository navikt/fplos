package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;
import java.util.Set;

public record OrganisasjonsEnhet(String enhetId, String navn, Set<String> temaer) {

    public boolean kanBehandleForeldrepenger() {
        return temaer.contains("FOR");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (OrganisasjonsEnhet) o;
        return enhetId.equals(that.enhetId) && navn.equals(that.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetId, navn);
    }
}
