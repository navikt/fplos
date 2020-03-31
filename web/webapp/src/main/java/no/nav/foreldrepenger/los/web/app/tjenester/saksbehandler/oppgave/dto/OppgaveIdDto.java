package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class OppgaveIdDto implements AbacDto {

    @JsonProperty("oppgaveId")
    @NotNull
    @Digits(integer = 18, fraction = 0)
    private final Long oppgaveId;

    public OppgaveIdDto() {
        oppgaveId = null; // NOSONAR
    }

    public OppgaveIdDto(Long oppgaveId) {
        Objects.requireNonNull(oppgaveId, "oppgaveId");
        this.oppgaveId = oppgaveId;
    }

    public OppgaveIdDto(String oppgaveId) {
        this.oppgaveId = Long.valueOf(oppgaveId);
    }

    @JsonIgnore
    public Long getVerdi() {
        return oppgaveId;
    }

    @Override
    public String toString() {
        return "FeatureToggleIdDto{" +
                "oppgaveId='" + oppgaveId + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
