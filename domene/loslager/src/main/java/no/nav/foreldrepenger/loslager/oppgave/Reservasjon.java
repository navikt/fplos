package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;

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

    public LocalDateTime getReservertTil() { return reservertTil; }

    public String getReservertAv() {
        return reservertAv;
    }

    public String getFlyttetAv() {
        return flyttetAv;
    }

    public LocalDateTime getFlyttetTidspunkt() { return flyttetTidspunkt; }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void reserverNormalt(){
        reservertTil = LocalDateTime.now().plusHours(8);
        reservertAv = finnBrukernavn();
        flyttetAv = null;
        flyttetTidspunkt = null;
        begrunnelse = null;
    }

    public void reserverOppgaveFraTidligereReservasjon(Reservasjon other) {
        this.reservertTil = other.reservertTil;
        this.reservertAv = other.reservertAv;
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

    public void flyttReservasjon(String brukernavn, String begrunnelseForFlytting) {
        reservertTil = reservertTil.plusHours(24);
        reservertAv = brukernavn;
        flyttetAv = finnBrukernavn();
        flyttetTidspunkt = LocalDateTime.now();
        begrunnelse = begrunnelseForFlytting;
    }

    private static String finnBrukernavn() {
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident.toUpperCase() : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

    public boolean erAktiv() {
        return reservertTil.isAfter(LocalDateTime.now());
    }
}
