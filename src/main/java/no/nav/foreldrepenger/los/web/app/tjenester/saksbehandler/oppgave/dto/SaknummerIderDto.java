package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SaknummerIderDto implements AbacDto {

    @JsonProperty("saksnummerListe")
    @NotNull
    @Pattern(regexp = "^[0-9,]+$")
    private final String saksnummerListe;

    public SaknummerIderDto() {
        saksnummerListe = null; // NOSONAR
    }

    public SaknummerIderDto(String saksnummerListe) {
        Objects.requireNonNull(saksnummerListe, "saksnummerListe");
        this.saksnummerListe = saksnummerListe;
    }

    public String getVerdi() {
        return saksnummerListe;
    }

    @Override
    public String toString() {
        return "SaknummerIderDto{" + "saksnummerListe='" + saksnummerListe + '\'' + '}';
    }

    public List<Long> getSaksnummerListe() {
        if (null == saksnummerListe) {
            return new ArrayList<>();
        }

        return Stream.of(saksnummerListe.split(",")).map(s -> Long.valueOf(s.trim())).toList();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
