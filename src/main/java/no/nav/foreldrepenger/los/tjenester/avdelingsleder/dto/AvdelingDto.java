package no.nav.foreldrepenger.los.tjenester.avdelingsleder.dto;

import java.util.Objects;

import jakarta.validation.constraints.NotNull;

public record AvdelingDto(@NotNull String avdelingEnhet, @NotNull String navn, @NotNull Boolean kreverKode6) {

    public AvdelingDto {
        Objects.requireNonNull(avdelingEnhet, "avdelingEnhet");
        Objects.requireNonNull(navn, "navn");
        Objects.requireNonNull(kreverKode6, "kreverKode6");
    }
}
