package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record BehandlingÅrsakDto(@JsonProperty("behandlingArsakType") BehandlingÅrsakType behandlingÅrsakType) {
}
