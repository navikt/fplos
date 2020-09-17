package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class OppgaveKriterieTypeDto implements AbacDto {
    @NotNull
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
