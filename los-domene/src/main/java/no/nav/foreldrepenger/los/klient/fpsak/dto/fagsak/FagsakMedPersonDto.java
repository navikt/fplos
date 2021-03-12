package no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.los.oppgave.FagsakStatus;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FagsakMedPersonDto {
    private Long saksnummer;
    private String saksnummerString;
    private FagsakYtelseType fagsakYtelseType;
    private FagsakStatus status;
    private PersonDto person;
    private LocalDate barnFodt;

    public FagsakMedPersonDto() {

    }

    public FagsakMedPersonDto(String saksnummer, FagsakYtelseType sakstype,
                              FagsakStatus status, PersonDto person,
                              LocalDate barnFodt) {
        this.saksnummer = Long.parseLong(saksnummer);
        this.saksnummerString = saksnummer;
        this.fagsakYtelseType = sakstype;
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

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
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
