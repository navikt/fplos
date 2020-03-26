package no.nav.fplos.foreldrepengerbehandling.dto.behandling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum BehandlingÅrsakType {
    BERØRT_BEHANDLING("BERØRT-BEHANDLING", "Endring i den andre forelderens uttak");

    private String navn;
    private String kode;

    BehandlingÅrsakType(String kode, String navn) {
        this.navn = navn;
        this.kode = kode;
    }

    @JsonCreator
    public static BehandlingÅrsakType fraKode(@JsonProperty("kode") String kode) {
        if (kode != null && kode.equals(BERØRT_BEHANDLING.kode)) {
            return BERØRT_BEHANDLING;
        }
        return null;
    }
}
