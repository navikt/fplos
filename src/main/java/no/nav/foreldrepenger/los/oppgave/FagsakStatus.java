package no.nav.foreldrepenger.los.oppgave;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.los.felles.Kodeverdi;

public enum FagsakStatus implements Kodeverdi {

    OPPRETTET("OPPR", "Opprettet"),
    UNDER_BEHANDLING("UBEH", "Under behandling"),
    LØPENDE("LOP", "Løpende"),
    AVSLUTTET("AVSLU", "Avsluttet");

    @JsonValue
    private String kode;
    @JsonIgnore
    private final String navn;

    FagsakStatus(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }


}
