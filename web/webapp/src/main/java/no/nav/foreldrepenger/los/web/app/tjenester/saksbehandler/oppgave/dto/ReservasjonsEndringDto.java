package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class ReservasjonsEndringDto implements AbacDto {

    @JsonProperty("oppgaveId")
    @NotNull
    @Valid
    private OppgaveIdDto oppgaveId;

    @JsonProperty("reserverTil")
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate reserverTil;

    public ReservasjonsEndringDto() {
    }

    public ReservasjonsEndringDto(OppgaveIdDto oppgaveId, LocalDate reserverTil) {
        Objects.requireNonNull(oppgaveId, "oppgaveId");
        Objects.requireNonNull(reserverTil, "reserverTil");
        this.oppgaveId = oppgaveId;
        this.reserverTil = reserverTil;
    }

    public OppgaveIdDto getOppgaveId() {
        return oppgaveId;
    }

    public LocalDate getReserverTil() {
        return reserverTil;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return oppgaveId.abacAttributter();
    }


}
