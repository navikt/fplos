package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class EventIdDto implements AbacDto {

    @JsonProperty("eventId")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private final Long eventId;

    public EventIdDto() {
        eventId = null; // NOSONAR
    }

    public EventIdDto(Long eventId) {
        Objects.requireNonNull(eventId, "eventId");
        this.eventId = eventId;
    }

    public EventIdDto(String eventId) {
        this.eventId = Long.valueOf(eventId);
    }

    @JsonIgnore
    public Long getVerdi() {
        return eventId;
    }

    @Override
    public String toString() {
        return "EventIdDto{" +
                "eventId='" + eventId + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
