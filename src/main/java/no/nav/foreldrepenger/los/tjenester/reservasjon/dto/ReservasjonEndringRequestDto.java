package no.nav.foreldrepenger.los.tjenester.reservasjon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import java.time.LocalDate;

public record ReservasjonEndringRequestDto(@JsonProperty("oppgaveId") @NotNull @Valid OppgaveIdDto oppgaveId,
                                           @JsonProperty("reserverTil") @NotNull LocalDate reserverTil) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return oppgaveId.abacAttributter();
    }
}
