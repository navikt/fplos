package no.nav.fplos.foreldrepengerbehandling.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.loslager.oppgave.FagsakStatus;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakMedPersonDto {
    private Long saksnummer;
    private String saksnummerString;
    private FagsakYtelseType sakstype;
    private FagsakStatus status;
    private PersonDto person;
    private LocalDate barnFodt;

    public FagsakMedPersonDto() {

    }

    public FagsakMedPersonDto(String saksnummerString, FagsakYtelseType sakstype,
                              FagsakStatus status, PersonDto person,
                              LocalDate barnFodt) {
        this.saksnummer = Long.parseLong(saksnummerString);
        this.saksnummerString = saksnummerString;
        this.sakstype = sakstype;
        this.status = status;
        this.person = person;
        this.barnFodt = barnFodt;
    }

    public FagsakMedPersonDto person(PersonDto person) {
        this.person = person;
        return this;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public String getSaksnummerString() {
        return saksnummerString;
    }

    public FagsakYtelseType getSakstype() {
        return sakstype;
    }

    public FagsakStatus getStatus() {
        return status;
    }

    public PersonDto getPerson() {
        return person;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }
}
