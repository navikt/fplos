package no.nav.foreldrepenger.loslager.oppgave;

import no.nav.foreldrepenger.loslager.BaseEntitet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity(name = "eventmottakFeillogg")
@Table(name = "EVENTMOTTAK_FEILLOGG")
public class EventmottakFeillogg extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTMOTTAK_FEILLOGG")
    private Long id;

    @Lob
    @Column(name = "MELDING", nullable = false)
    private String melding;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status = Status.FEILET;

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
        return status.name();
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public EventmottakFeillogg markerFerdig() {
        this.status = Status.FERDIG;
        return this;
    }

    public enum Status {
        FEILET, FERDIG;
    }
}
