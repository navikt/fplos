package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

@Entity(name = "Reservasjon")
@Table(name = "RESERVASJON")
public class Reservasjon extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESERVASJON")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "oppgave_id", nullable = false, updatable = false)
    private Oppgave oppgave;

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

    public Reservasjon(){
        //CDI
    }

    public Reservasjon(Oppgave oppgave) {
        this.oppgave = oppgave;
        reservertAv = finnBrukernavn();
    }

    public Long getId() {
        return id;
    }

    public Oppgave getOppgave() {
        return oppgave;
    }

    public LocalDateTime getReservertTil() {
        return reservertTil;
    }

    public String getReservertAv() {
        return reservertAv;
    }

    public Optional<String> getFlyttetAv() {
        return Optional.ofNullable(flyttetAv);
    }

    public LocalDateTime getFlyttetTidspunkt() {
        return flyttetTidspunkt;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void frigiReservasjon(String begrunnelse) {
        reservertTil = LocalDateTime.now().minusSeconds(1);
        if (reservertAv == null) {
            reservertAv = finnBrukernavn();
        }
        flyttetAv = null;
        flyttetTidspunkt = null;
        this.begrunnelse = begrunnelse;
    }

    public void forlengReservasjonPåOppgave() {
        reservertTil = reservertTil.plusHours(24);
        reservertAv = finnBrukernavn();
    }

    public void endreReservasjonPåOppgave(LocalDateTime reservertTil) {
        this.reservertTil = reservertTil;
    }

    public void flyttReservasjon(String brukernavn, String begrunnelseForFlytting) {
        reservertTil = reservertTil.plusHours(24);
        reservertAv = brukernavn;
        flyttetAv = finnBrukernavn();
        flyttetTidspunkt = LocalDateTime.now();
        begrunnelse = begrunnelseForFlytting;
    }

    public boolean erAktiv() {
        return reservertTil != null && reservertTil.isAfter(LocalDateTime.now());
    }

    public void setReservertTil(LocalDateTime dateTime) {
        this.reservertTil = dateTime;
    }

    public void setReservertAv(String reservertAv) {
        this.reservertAv = reservertAv;
    }

    public void setFlyttetAv(String flyttetAv) {
        this.flyttetAv = flyttetAv;
    }

    public void setFlyttetTidspunkt(LocalDateTime flyttetTidspunkt) {
        this.flyttetTidspunkt = flyttetTidspunkt;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }
}
