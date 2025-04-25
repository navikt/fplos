package no.nav.foreldrepenger.los.tjenester.saksbehandler.oppgave.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public class OpphevTilknyttetReservasjonRequestDto implements AbacDto {

    @NotNull
    @Valid
    private OppgaveIdDto oppgaveId;

    @Size(max = 500)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String begrunnelse;

    public OpphevTilknyttetReservasjonRequestDto() {
    }

    public OpphevTilknyttetReservasjonRequestDto(OppgaveIdDto oppgaveId, String begrunnelse) {
        this.oppgaveId = oppgaveId;
        this.begrunnelse = begrunnelse;
    }

    public OppgaveIdDto getOppgaveId() {
        return oppgaveId;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    @Override
    public String toString() {
        return "OppgaveOpphevingDto{" + "oppgaveId=" + oppgaveId + ", begrunnelse='***'" + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return oppgaveId.abacAttributter();
    }
}
