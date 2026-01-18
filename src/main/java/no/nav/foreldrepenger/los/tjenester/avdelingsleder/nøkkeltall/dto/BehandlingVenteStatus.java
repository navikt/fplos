package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum BehandlingVenteStatus implements Kodeverdi {
    PÅ_VENT("PÅ_VENT", "På vent"),
    IKKE_PÅ_VENT("IKKE_PÅ_VENT", "Ikke på vent");

    @JsonValue
    private final String kode;
    @JsonIgnore
    private final String navn;

    BehandlingVenteStatus(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @Override
    public String getNavn() {
        return navn;
    }

    @Override
    public String getKode() {
        return kode;
    }
}
