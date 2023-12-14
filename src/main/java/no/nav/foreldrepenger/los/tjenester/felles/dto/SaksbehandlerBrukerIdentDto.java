package no.nav.foreldrepenger.los.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class SaksbehandlerBrukerIdentDto implements AbacDto {

    @JsonProperty("brukerIdent")
    @NotNull
    @Size(max = 100)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String brukerIdent;

    public SaksbehandlerBrukerIdentDto() {
    }

    public SaksbehandlerBrukerIdentDto(String brukerIdent) {
        Objects.requireNonNull(brukerIdent, "brukerIdent");
        this.brukerIdent = brukerIdent;
    }

    @JsonIgnore
    public String getVerdi() {
        return brukerIdent;
    }

    @Override
    public String toString() {
        return "SaksbehandlerBrukerIdentDto{" + "brukerIdent='" + brukerIdent + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaksbehandlerBrukerIdentDto)) {
            return false;
        }

        var dto = (SaksbehandlerBrukerIdentDto) o;
        return brukerIdent.equals(dto.brukerIdent);
    }

    @Override
    public int hashCode() {
        return 31 * brukerIdent.hashCode();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
