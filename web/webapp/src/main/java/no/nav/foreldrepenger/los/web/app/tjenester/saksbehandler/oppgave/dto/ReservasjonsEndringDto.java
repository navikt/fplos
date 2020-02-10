package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class ReservasjonsEndringDto implements AbacDto {

    @JsonProperty("oppgaveId")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private Long oppgaveId;

    @JsonProperty("reserverTil")
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate reserverTil;

    public ReservasjonsEndringDto() {
    }

    public ReservasjonsEndringDto(Long oppgaveId, LocalDate reserverTil) {
        Objects.requireNonNull(oppgaveId, "oppgaveId");
        Objects.requireNonNull(reserverTil, "reserverTil");
        this.oppgaveId = oppgaveId;
        this.reserverTil = reserverTil;
    }

    public Long getOppgaveId() {
        return oppgaveId;
    }

    public LocalDate getReserverTil() {
        return reserverTil;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }


}
