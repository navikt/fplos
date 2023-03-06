package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class OppgaveIderDto implements AbacDto {

    @JsonProperty("oppgaveIder")
    @NotNull
    @Pattern(regexp = "^[0-9,]+$")
    private String oppgaveIder;

    public OppgaveIderDto() {
        // Jackson
    }

    public OppgaveIderDto(String oppgaveIder) {
        Objects.requireNonNull(oppgaveIder, "oppgaveIder");
        this.oppgaveIder = oppgaveIder;
    }

    public String getVerdi() {
        return oppgaveIder;
    }

    @Override
    public String toString() {
        return "OppgaveIderDto{" + "oppgaveId='" + oppgaveIder + '\'' + '}';
    }

    public List<Long> getOppgaveIdeer() {
        if (null == oppgaveIder) {
            return new ArrayList<>();
        }
        return Arrays.stream(oppgaveIder.trim().split(",")).map(Long::valueOf).toList();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
