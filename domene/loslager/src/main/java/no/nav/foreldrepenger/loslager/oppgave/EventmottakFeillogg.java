package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.loslager.BaseEntitet;

@Entity(name = "eventmottakFeillogg")
@Table(name = "EVENTMOTTAK_FEILLOGG")
public class EventmottakFeillogg extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTMOTTAK_FEILLOGG")
    private Long id;

    @Lob
    @Column(name = "MELDING", nullable = false)
    private String melding;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "STATUS", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + EventmottakStatus.DISCRIMINATOR + "'"))
    private EventmottakStatus status;

    @Column(name = "ANTALL_FEILEDE_FORSOK")
    private Long antallFeiledeForsøk = 0L;

    @Column(name = "SISTE_KJORING_TS", nullable = false)
    private LocalDateTime sisteKjøringTS;

    @Lob
    @Column(name = "FEILMELDING_SISTE_KJORING")
    private String feilmeldingSisteKjøring;

    EventmottakFeillogg(){
        //For å kunne automatisk generere
    }

    public EventmottakFeillogg(String melding, EventmottakStatus status, LocalDateTime sisteKjøringTS, String feilmeldingSisteKjøring) {
        this.melding = melding;
        this.status = status;
        this.sisteKjøringTS = sisteKjøringTS;
        this.feilmeldingSisteKjøring = feilmeldingSisteKjøring;
    }

    public Long getId() {
        return id;
    }

    public String getMelding() {
        return melding;
    }

    public EventmottakStatus getStatus() {
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

    public EventmottakFeillogg endreStatus(EventmottakStatus nyStatus) {
        this.status = nyStatus;
        return this;
    }
}
