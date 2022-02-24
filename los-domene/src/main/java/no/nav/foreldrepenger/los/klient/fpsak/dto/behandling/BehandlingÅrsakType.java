package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingÅrsakType {
    BERØRT_BEHANDLING("BERØRT-BEHANDLING", "Endring i den andre forelderens uttak"),
    RE_ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER", "Endringssøknad fra bruker"),
    RE_VEDTAK_PLEIEPENGER("RE-VEDTAK-PSB", "Pleiepenger");

    private final String navn;
    @JsonValue
    private final String kode;

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
}
