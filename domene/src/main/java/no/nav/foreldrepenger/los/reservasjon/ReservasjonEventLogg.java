package no.nav.foreldrepenger.los.reservasjon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;

@Entity(name = "reservasjonEventLogg")
@Table(name = "RESERVASJON_EVENT_LOGG")
public class ReservasjonEventLogg extends BaseEntitet {

    // Todo: vurder sanering ved innføring en-til-mange forhold mellom Oppgave-Reservasjon.
    // må ta vare på historikk tilsvarende reservasjonseventlogg

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

    public ReservasjonEventLogg() {
        //For automatisk generering
    }

    public ReservasjonEventLogg(Reservasjon reservasjon) {
        this.reservasjonId = reservasjon.getId();
        this.oppgaveId = reservasjon.getOppgave().getId();
        this.reservertTil = reservasjon.getReservertTil();
        this.reservertAv = reservasjon.getReservertAv();
        this.flyttetAv = reservasjon.getFlyttetAv();
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


    public static final class Builder {
        private Long reservasjonId;
        private Long oppgaveId;
        private LocalDateTime reservertTil;
        private String reservertAv;
        private String flyttetAv;
        private LocalDateTime flyttetTidspunkt;
        private String begrunnelse;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder reservasjonId(Long reservasjonId) {
            this.reservasjonId = reservasjonId;
            return this;
        }

        public Builder oppgaveId(Long oppgaveId) {
            this.oppgaveId = oppgaveId;
            return this;
        }

        public Builder reservertTil(LocalDateTime reservertTil) {
            this.reservertTil = reservertTil;
            return this;
        }

        public Builder reservertAv(String reservertAv) {
            this.reservertAv = reservertAv;
            return this;
        }

        public Builder flyttetAv(String flyttetAv) {
            this.flyttetAv = flyttetAv;
            return this;
        }

        public Builder flyttetTidspunkt(LocalDateTime flyttetTidspunkt) {
            this.flyttetTidspunkt = flyttetTidspunkt;
            return this;
        }

        public Builder begrunnelse(String begrunnelse) {
            this.begrunnelse = begrunnelse;
            return this;
        }

        public ReservasjonEventLogg build() {
            ReservasjonEventLogg reservasjonEventLogg = new ReservasjonEventLogg();
            reservasjonEventLogg.reservertAv = this.reservertAv;
            reservasjonEventLogg.begrunnelse = this.begrunnelse;
            reservasjonEventLogg.flyttetAv = this.flyttetAv;
            reservasjonEventLogg.reservertTil = this.reservertTil;
            reservasjonEventLogg.flyttetTidspunkt = this.flyttetTidspunkt;
            reservasjonEventLogg.oppgaveId = this.oppgaveId;
            reservasjonEventLogg.reservasjonId = this.reservasjonId;
            return reservasjonEventLogg;
        }
    }
}
