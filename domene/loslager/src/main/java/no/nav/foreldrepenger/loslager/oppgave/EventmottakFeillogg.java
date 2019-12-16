package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.BaseEntitet;

@Entity(name = "eventmottakFeillogg")
@Table(name = "EVENTMOTTAK_FEILLOGG")
public class EventmottakFeillogg extends BaseEntitet {
    private static final String FERDIG = "FERDIG";
    private static final String FEILET = "FEILET";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTMOTTAK_FEILLOGG")
    private Long id;

    @Lob
    @Column(name = "MELDING", nullable = false)
    private String melding;

    @Column(name = "STATUS")
    private String status = FEILET;

    // todo: fjern denne + databasekolonnen i contract. ingenting inkrementerer denne.. ubrukt funksjonalitet
    //@Column(name = "ANTALL_FEILEDE_FORSOK")
    //private Long antallFeiledeForsøk = 0L;

    // todo: fjern fra db. Gitt punkt over dekkes denne av TID_OPPRETTET i BaseEntitet
    //@Column(name = "SISTE_KJORING_TS", nullable = false)
    //private LocalDateTime sisteKjøringTS = LocalDateTime.now();

    @Lob
    @Column(name = "FEILMELDING_SISTE_KJORING")
    private String feilmelding;

    EventmottakFeillogg() {
        // Rammeverk
    }

    public EventmottakFeillogg(String melding, String feilmelding) {
        this.melding = melding;
        this.feilmelding = feilmelding;
    }

    public Long getId() {
        return id;
    }

    public String getMelding() {
        return melding;
    }

    public String getStatus() {
        return status;
    }

    //public Long getAntallFeiledeForsøk() {
        //return antallFeiledeForsøk;
    //}

//    public LocalDateTime getSisteKjøringTS() {
//        return sisteKjøringTS;
//    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public EventmottakFeillogg markerFerdig() {
        this.status = FERDIG;
        return this;
    }
}
