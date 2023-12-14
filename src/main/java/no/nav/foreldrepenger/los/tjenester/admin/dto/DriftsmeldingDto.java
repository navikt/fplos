package no.nav.foreldrepenger.los.tjenester.admin.dto;


import java.time.LocalDateTime;

public class DriftsmeldingDto {
    private String id;
    private String melding;
    private LocalDateTime aktivFra;
    private LocalDateTime aktivTil;

    public DriftsmeldingDto(String id, String melding, LocalDateTime aktivFra, LocalDateTime aktivTil) {
        this.id = id;
        this.melding = melding;
        this.aktivFra = aktivFra;
        this.aktivTil = aktivTil;
    }

    public String getId() {
        return id;
    }

    public String getMelding() {
        return melding;
    }

    public LocalDateTime getAktivFra() {
        return aktivFra;
    }

    public LocalDateTime getAktivTil() {
        return aktivTil;
    }

}

