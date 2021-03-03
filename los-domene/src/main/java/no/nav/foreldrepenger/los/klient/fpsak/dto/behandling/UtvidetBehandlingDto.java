package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtvidetBehandlingDto extends BehandlingDto {

    @JsonProperty("ansvarligSaksbehandler")
    private String ansvarligSaksbehandler;

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }
}

