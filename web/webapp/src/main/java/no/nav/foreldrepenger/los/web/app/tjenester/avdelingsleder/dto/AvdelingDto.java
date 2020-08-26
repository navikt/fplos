package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;

import java.util.Objects;

public class AvdelingDto {

    private Long id;
    private String avdelingEnhet;
    private String navn;
    private Boolean kreverKode6;

    @JsonCreator
    public AvdelingDto(@JsonProperty("id") Long id,
                       @JsonProperty("avdelingEnhet") String avdelingEnhet,
                       @JsonProperty("navn") String navn,
                       @JsonProperty("kreverKode6") Boolean kreverKode6) {
        this.id = Objects.requireNonNull(id, "id");
        this.avdelingEnhet = Objects.requireNonNull(avdelingEnhet, "avdelingEnhet");
        this.navn = Objects.requireNonNull(navn, "navn");
        this.kreverKode6 = Objects.requireNonNull(kreverKode6, "kreverKode6");
    }

    public Long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public String getAvdelingEnhet() {
        return avdelingEnhet;
    }

    public Boolean getKreverKode6() {
        return kreverKode6;
    }

    @JsonIgnore
    public Avdeling getValue() {
        return new Avdeling(avdelingEnhet, navn, kreverKode6);
    }
}
