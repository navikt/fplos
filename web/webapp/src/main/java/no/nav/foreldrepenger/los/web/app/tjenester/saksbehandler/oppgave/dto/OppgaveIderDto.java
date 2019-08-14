package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class OppgaveIderDto implements AbacDto {

    @JsonProperty("oppgaveIder")
    @NotNull
    private final String oppgaveIder;

    public OppgaveIderDto() {
        oppgaveIder = null; // NOSONAR
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
        return "OppgaveIderDto{" +
                "oppgaveId='" + oppgaveIder + '\'' +
                '}';
    }

    public List<Long> getOppgaveIdeer(){
        if (null == oppgaveIder){
            return new ArrayList<>();
        }
        return Arrays.asList(oppgaveIder.trim().split("_")).stream().map(opgid -> Long.valueOf(opgid)).collect(Collectors.toList());
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}