package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum BehandlingÅrsakType {
    BERØRT_BEHANDLING("BERØRT-BEHANDLING", "Endring i den andre forelderens uttak"),
    RE_ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER", "Endringssøknad fra bruker");

    private String navn;
    private String kode;

    BehandlingÅrsakType(String kode, String navn) {
        this.navn = navn;
        this.kode = kode;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    @JsonCreator
    public static BehandlingÅrsakType fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(values())
                .filter(v -> v.kode.equals(kode))
                .findFirst()
                .orElse(null);
    }
}
