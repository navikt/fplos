package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class SaksnummerDto {

    @JsonProperty("saksnummer")
    @NotNull
    @Pattern(regexp = "^[0-9_]+$")
    private final String saksnummer;

    public SaksnummerDto(String saksnummer) {
        this.saksnummer = saksnummer;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    @Override
    public String toString() {
        return "SaksnummerDto{" +
                "saksnummer='" + saksnummer + '\'' +
                '}';
    }
}
