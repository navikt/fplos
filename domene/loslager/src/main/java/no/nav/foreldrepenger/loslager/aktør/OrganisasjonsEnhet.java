package no.nav.foreldrepenger.loslager.aktør;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class OrganisasjonsEnhet {

    private final String enhetId;
    private final String enhetNavn;

    @JsonCreator
    public OrganisasjonsEnhet(@JsonProperty("enhetId") String enhetId,
                              @JsonProperty("navn") String enhetNavn) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
    }

    public String getEnhetId() { return enhetId; }

    public String getEnhetNavn(){ return enhetNavn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisasjonsEnhet that = (OrganisasjonsEnhet) o;
        return enhetId.equals(that.enhetId) &&
                enhetNavn.equals(that.enhetNavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetId, enhetNavn);
    }

    @Override
    public String toString() {
        return "OrganisasjonsEnhet{" +
                "enhetId='" + enhetId + '\'' +
                ", enhetNavn='" + enhetNavn + '\'' +
                '}';
    }
}
