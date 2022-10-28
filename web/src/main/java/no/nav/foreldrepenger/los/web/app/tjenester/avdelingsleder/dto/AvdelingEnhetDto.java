package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public class AvdelingEnhetDto implements AbacDto {

    @JsonProperty("avdelingEnhet")
    @NotNull
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private final String avdelingEnhet;

    public AvdelingEnhetDto() {
        avdelingEnhet = null; // NOSONAR
    }

    public AvdelingEnhetDto(String avdelingEnhet) {
        Objects.requireNonNull(avdelingEnhet, "avdelingEnhet");
        this.avdelingEnhet = avdelingEnhet;
    }

    public String getAvdelingEnhet(){
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "AvdelingEnhetDto{" +
                "avdelingEnhet='" + avdelingEnhet + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet);
    }
}

