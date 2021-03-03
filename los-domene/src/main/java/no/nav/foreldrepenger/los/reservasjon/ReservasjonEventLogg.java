package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;

@Entity(name = "reservasjonEventLogg")
@Table(name = "RESERVASJON_EVENT_LOGG")
public class ReservasjonEventLogg extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESERVASJON_EVENT_LOGG")
    private Long id;

    @Column(name = "RESERVASJON_ID", nullable = false)
    private Long reservasjonId;

    @Column(name = "OPPGAVE_ID", nullable = false)
    private Long oppgaveId;

    @Column(name = "RESERVERT_TIL")
    private LocalDateTime reservertTil;

    @Column(name = "RESERVERT_AV")
    private String reservertAv;

    @Column(name = "FLYTTET_AV")
    private String flyttetAv;

    @Column(name = "FLYTTET_TIDSPUNKT")
    private LocalDateTime flyttetTidspunkt;

    @Column(name = "BEGRUNNELSE")
    private String begrunnelse;

    public ReservasjonEventLogg(){
        //For automatisk generering
    }

    public ReservasjonEventLogg(Reservasjon reservasjon) {
        this.reservasjonId = reservasjon.getId();
        this.oppgaveId = reservasjon.getOppgave().getId();
        this.reservertTil = reservasjon.getReservertTil();
        this.reservertAv = reservasjon.getReservertAv();
        this.flyttetAv = reservasjon.getFlyttetAv().orElse(null);
        this.flyttetTidspunkt = reservasjon.getFlyttetTidspunkt();
        this.begrunnelse = reservasjon.getBegrunnelse();
    }

    public Long getId() {
        return id;
    }

    public Long getReservasjonId() {
        return reservasjonId;
    }

    public Long getOppgaveId() {
        return oppgaveId;
    }

    public LocalDateTime getReservertTil() {
        return reservertTil;
    }

    public String getReservertAv() {
        return reservertAv;
    }

    public String getFlyttetAv() {
        return flyttetAv;
    }

    public LocalDateTime getFlyttetTidspunkt() {
        return flyttetTidspunkt;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }
}
