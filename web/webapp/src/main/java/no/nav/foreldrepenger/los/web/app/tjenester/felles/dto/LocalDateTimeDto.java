package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class LocalDateTimeDto implements AbacDto {
    @JsonProperty("tidspunkt")
    @NotNull
    private final LocalDateTime tidspunkt;

    public LocalDateTimeDto(LocalDateTime tidspunkt) {
        Objects.requireNonNull(tidspunkt, "tidspunkt");
        this.tidspunkt = tidspunkt;
    }

    public LocalDateTime getVerdi() {
        return tidspunkt;
    }

    @Override
    public String toString() {
        ZonedDateTime zonedDateTime = tidspunkt.atZone(ZoneId.of("Europe/Oslo"));
        return "LocalDateTimeDto{" +
                "tidspunkt='" + zonedDateTime.toInstant().toEpochMilli() + '\'' +
                '}';
    }
    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }

}
