package no.nav.foreldrepenger.los.klient.fpsak.dto.kodeverk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KodeDto(String kodeverk, String kode) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static KodeDto fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        return TempAvledeKode.getVerdiKodeDto(node, "kode");
    }

}
