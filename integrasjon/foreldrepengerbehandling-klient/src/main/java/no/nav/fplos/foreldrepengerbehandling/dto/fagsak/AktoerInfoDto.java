package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.domene.typer.AktørId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AktoerInfoDto {
    private AktørId aktoerId;
    private PersonDto person;

    public void setAktoerId(AktørId aktoerId) {
        this.aktoerId = aktoerId;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public AktørId getAktoerId() {
        return aktoerId;
    }

    public PersonDto getPerson() {
        return person;
    }
}
