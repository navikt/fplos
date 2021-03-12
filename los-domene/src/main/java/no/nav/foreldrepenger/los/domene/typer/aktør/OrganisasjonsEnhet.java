package no.nav.foreldrepenger.los.domene.typer.aktør;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganisasjonsEnhet {

    private final String enhetId;
    private final String enhetNavn;
    private final Set<String> fagområder;

    @JsonCreator
    public OrganisasjonsEnhet(@JsonProperty("enhetId") String enhetId,
                              @JsonProperty("navn") String enhetNavn,
                              @JsonProperty("fagomrader") Set<String> fagområder) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
        this.fagområder = fagområder;
    }

    public String getEnhetId() { return enhetId; }

    public String getEnhetNavn(){ return enhetNavn; }

    public boolean kanBehandleForeldrepenger() {
        return fagområder.contains("FOR");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (OrganisasjonsEnhet) o;
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
