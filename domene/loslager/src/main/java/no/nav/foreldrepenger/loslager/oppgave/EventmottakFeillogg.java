package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDateTime;

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
    private String status;

    @Column(name = "ANTALL_FEILEDE_FORSOK")
    private Long antallFeiledeForsøk = 0L;

    @Column(name = "SISTE_KJORING_TS", nullable = false)
    private LocalDateTime sisteKjøringTS;

    @Lob
    @Column(name = "FEILMELDING_SISTE_KJORING")
    private String feilmeldingSisteKjøring;

    EventmottakFeillogg() {
        //For å kunne automatisk generere
    }

    public EventmottakFeillogg(String melding, LocalDateTime sisteKjøringTS, String feilmeldingSisteKjøring) {
        this.melding = melding;
        this.status = FEILET;
        this.sisteKjøringTS = sisteKjøringTS;
        this.feilmeldingSisteKjøring = feilmeldingSisteKjøring;
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

    public Long getAntallFeiledeForsøk() {
        return antallFeiledeForsøk;
    }

    public LocalDateTime getSisteKjøringTS() {
        return sisteKjøringTS;
    }

    public String getFeilmeldingSisteKjøring() {
        return feilmeldingSisteKjøring;
    }

    public EventmottakFeillogg markerFerdig() {
        this.status = FERDIG;
        return this;
    }
}
