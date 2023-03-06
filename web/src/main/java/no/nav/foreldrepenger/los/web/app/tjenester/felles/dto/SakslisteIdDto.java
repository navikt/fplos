package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import java.util.Objects;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SakslisteIdDto that = (SakslisteIdDto) o;
        return sakslisteId.equals(that.sakslisteId) && abacAttributter().equals(that.abacAttributter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sakslisteId);
    }

}
