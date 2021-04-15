package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganisasjonsEnhet(String id, String navn, Set<String> fagområder) {

    @JsonCreator
    public OrganisasjonsEnhet(@JsonProperty("enhetId") String id,
                              @JsonProperty("navn") String navn,
                              @JsonProperty("fagomrader") Set<String> fagområder) {
        this.id = id;
        this.navn = navn;
        this.fagområder = fagområder;
    }

    public boolean kanBehandleForeldrepenger() {
        return fagområder.contains("FOR");
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
        return id.equals(that.id) && navn.equals(that.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, navn);
    }
}
