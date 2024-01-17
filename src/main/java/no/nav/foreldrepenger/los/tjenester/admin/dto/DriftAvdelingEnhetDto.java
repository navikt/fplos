package no.nav.foreldrepenger.los.tjenester.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

import java.util.Objects;

public class DriftAvdelingEnhetDto implements AbacDto {

    @JsonProperty("avdelingEnhet")
    @NotNull
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private final String avdelingEnhet;

    public DriftAvdelingEnhetDto() {
        avdelingEnhet = null; // NOSONAR
    }

    public DriftAvdelingEnhetDto(String avdelingEnhet) {
        Objects.requireNonNull(avdelingEnhet, "avdelingEnhet");
        this.avdelingEnhet = avdelingEnhet;
    }

    public String getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "AvdelingEnhetDto{" + "avdelingEnhet='" + avdelingEnhet + '\'' + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}

