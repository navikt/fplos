package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import no.nav.foreldrepenger.los.domene.typer.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;

public record PersonDto(String navn, Integer alder, String personnummer, Boolean erKvinne,
                        String diskresjonskode, LocalDate dødsdato) {

    public PersonDto(Person person) {
        this(person.getNavn(), (int) ChronoUnit.YEARS.between(person.getFødselsdato(), LocalDate.now()),
                person.getFødselsnummer().value(), NavBrukerKjønn.K.equals(person.getKjønn()),
                person.getDiskresjonskode(), person.getDødsdato());
    }

    @Override
    public String toString() {
        //Sensitiv
        return "Person***";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonDto)) {
            return false;
        }

        var personDto = (PersonDto) o;

        if (!navn.equals(personDto.navn)) {
            return false;
        }
        if (!alder.equals(personDto.alder)) {
            return false;
        }
        if (!personnummer.equals(personDto.personnummer)) {
            return false;
        }
        return erKvinne.equals(personDto.erKvinne);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnummer);
    }
}
