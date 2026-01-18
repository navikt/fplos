package no.nav.foreldrepenger.los.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum OppgaveBehandlingStatus implements Kodeverdi {
    PÅ_VENT("PÅ_VENT", "På vent"),
    FERDIG("FERDIG", "Ferdig"),
    TIL_BESLUTTER("TIL_BESLUTTER", "Til beslutter"),
    RETURNERT_FRA_BESLUTTER("RETURNERT_FRA_BESLUTTER", "Returnert fra beslutter"),
    UNDER_ARBEID("UNDER_ARBEID", "Under arbeid");

    @JsonValue
    private String kode;
    @JsonIgnore
    private final String navn;

    OppgaveBehandlingStatus(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getNavn() {
        return navn;
    }
}
