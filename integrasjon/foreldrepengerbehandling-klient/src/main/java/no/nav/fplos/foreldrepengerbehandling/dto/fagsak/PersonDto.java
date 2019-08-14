package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDto {

    @JsonProperty("navn")
    private String navn;

    @JsonProperty("alder")
    private Integer alder;

    @JsonProperty("personnummer")
    private String personnummer;

    @JsonProperty("erKvinne")
    private Boolean erKvinne;

    @JsonProperty("diskresjonskode")
    private String diskresjonskode;

    @JsonProperty("dodsdato")
    private LocalDate dodsdato;

    public PersonDto() {
        // Injiseres i test
    }

    public PersonDto(String navn, Integer alder, String personnummer, boolean erKvinne, String diskresjonskode, LocalDate dodsdato) {
        this.navn = navn;
        this.alder = alder;
        this.personnummer = personnummer;
        this.erKvinne = erKvinne;
        this.diskresjonskode = diskresjonskode;
        this.dodsdato = dodsdato;
    }

    public String getNavn() {
        return navn;
    }

    public Integer getAlder() {
        return alder;
    }

    public String getPersonnummer() {
        return personnummer;
    }

    public Boolean getErKvinne() {
        return erKvinne;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

    public LocalDate getDodsdato() {
        return dodsdato;
    }

    @Override
    public String toString() {
        return "<navn='" + navn + '\'' +
            ", alder=" + alder +
            ", personnummer='" + personnummer + '\'' +
            ", erKvinne=" + erKvinne +
            '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PersonDto))
            return false;

        PersonDto personDto = (PersonDto) o;

        if (!navn.equals(personDto.navn))
            return false;
        if (!alder.equals(personDto.alder))
            return false;
        if (!personnummer.equals(personDto.personnummer))
            return false;
        return erKvinne.equals(personDto.erKvinne);
    }

    @Override
    public int hashCode() {
        int result = navn.hashCode();
        result = 31 * result + alder.hashCode();
        result = 31 * result + personnummer.hashCode();
        result = 31 * result + erKvinne.hashCode();
        return result;
    }
}
