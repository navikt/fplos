package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public class AvdelingOpprettelseDto implements AbacDto {
    @Pattern(regexp = "^[0-9]{4}$")
    private String avdelingKode;
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String avdelingNavn;
    private Boolean kreverKode6Tilgang;

    @JsonCreator
    public AvdelingOpprettelseDto(@JsonProperty("avdelingKode") String avdelingKode,
                                  @JsonProperty("avdelingNavn") String avdelingNavn,
                                  @JsonProperty("kreverKode6Tilgang") Boolean kreverKode6Tilgang) {
        this.avdelingKode = avdelingKode;
        this.avdelingNavn = avdelingNavn;
        this.kreverKode6Tilgang = kreverKode6Tilgang;
    }

    public String getAvdelingKode() {
        return avdelingKode;
    }

    public String getAvdelingNavn() {
        return avdelingNavn;
    }

    public Boolean getKreverKode6Tilgang() {
        return kreverKode6Tilgang;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
