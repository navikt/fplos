package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    @JsonProperty("behandlingArsakType")
    private BehandlingÅrsakType behandlingÅrsakType;

    public BehandlingÅrsakType getBehandlingÅrsakType() {
        return behandlingÅrsakType;
    }
}
