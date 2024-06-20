package no.nav.foreldrepenger.los.tjenester.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.foreldrepenger.los.felles.util.RegexPatterns;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public record DriftOpprettAvdelingEnhetDto(@NotNull @Pattern(regexp = RegexPatterns.ENHETSNUMMER) String enhetsnummer,
                                           @NotNull @Pattern(regexp = InputValideringRegex.FRITEKST) String enhetsnavn) implements AbacDto {
    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
