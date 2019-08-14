package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class AvdelingEnhetDto implements AbacDto {

    @JsonProperty("avdelingEnhet")
    @NotNull
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
        return AbacDataAttributter.opprett().leggTilOppgavestyringEnhet(avdelingEnhet);
    }
}

