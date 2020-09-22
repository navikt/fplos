package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public class OppgaveKriterieTypeDto implements AbacDto {
    @NotNull
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private final String oppgaveEgenskap;

    @JsonCreator
    public OppgaveKriterieTypeDto(@JsonProperty("oppgaveEgenskap") String oppgaveEgenskap) {
        Objects.requireNonNull(oppgaveEgenskap, "oppgaveEgenskap");
        this.oppgaveEgenskap = oppgaveEgenskap;
    }

    @JsonIgnore
    public String getVerdi() {
        return oppgaveEgenskap;
    }

    @Override
    public String toString() {
        return "OppgaveIdDto{" +
                "oppgaveId=" + oppgaveEgenskap +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }

}
