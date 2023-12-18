package no.nav.foreldrepenger.los.tjenester.reservasjon.dto;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class ReservasjonsEndringRequestDto implements AbacDto {

    @JsonProperty("oppgaveId")
    @NotNull
    @Valid
    private OppgaveIdDto oppgaveId;

    @JsonProperty("reserverTil")
    @NotNull
    private LocalDate reserverTil;

    public ReservasjonsEndringRequestDto() {
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
