package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class SakslisteIdDto implements AbacDto {

    @JsonProperty("sakslisteId")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private final Long sakslisteId;

    public SakslisteIdDto() {
        sakslisteId = null; // NOSONAR
    }

    public SakslisteIdDto(Long sakslisteId) {
        Objects.requireNonNull(sakslisteId, "sakslisteId");
        this.sakslisteId = sakslisteId;
    }

    public SakslisteIdDto(String sakslisteId) {
        this.sakslisteId = Long.valueOf(sakslisteId);
    }

    public Long getVerdi() {
        return sakslisteId;
    }

    @Override
    public String toString() {
        return "SaksnummerDto{" +
                "sakslisteId='" + sakslisteId + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}