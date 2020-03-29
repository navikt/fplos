package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    private BehandlingÅrsakType behandlingÅrsakType;

    @JsonCreator
    public BehandlingÅrsakDto(@JsonProperty("behandlingArsakType") BehandlingÅrsakType behandlingÅrsakType) {
        this.behandlingÅrsakType = behandlingÅrsakType;
    }

    public BehandlingÅrsakType getBehandlingÅrsakType() {
        return behandlingÅrsakType;
    }
}
