package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto;

import java.util.Objects;

public class AvdelingDto {
    private final Long id;
    private final String avdelingEnhet;
    private final String navn;
    private final boolean kreverKode6;

    public AvdelingDto(Long id, String avdelingEnhet, String navn, Boolean kreverKode6) {
        this.id = Objects.requireNonNull(id, "id");
        this.avdelingEnhet = Objects.requireNonNull(avdelingEnhet, "avdelingEnhet");
        this.navn = Objects.requireNonNull(navn, "navn");
        this.kreverKode6 = Objects.requireNonNull(kreverKode6, "kreverKode6");
    }

    public Long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public String getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public boolean getKreverKode6() {
        return kreverKode6;
    }
}
