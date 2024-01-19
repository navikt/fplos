package no.nav.foreldrepenger.los.tjenester.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.foreldrepenger.los.felles.util.RegexPatterns;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public record DriftAvdelingEnhetDto(@JsonProperty("avdelingEnhet")
                                    @NotNull
                                    @Pattern(regexp = RegexPatterns.ENHETSNUMMER)
                                    String avdelingEnhet) implements AbacDto {
    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
