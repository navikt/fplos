package no.nav.foreldrepenger.los.oppgave;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;


@Entity(name = "TilbakekrevingOppgave")
@PrimaryKeyJoinColumn(name = "OPPGAVE_ID")
@Table(name = "TILBAKEKREVING_EGENSKAPER")
public class TilbakekrevingOppgave extends Oppgave {

    @Column(name = "BELOP")
    private BigDecimal beløp;

    @Column(name = "FEILUTBETALINGSTART")
    protected LocalDateTime feilutbetalingstart;

    public BigDecimal getBeløp() {
        return beløp;
    }

    public LocalDateTime getFeilutbetalingstart() {
        return feilutbetalingstart;
    }

    public void avstemMed(TilbakekrevingOppgave other) {
        this.behandlingOpprettet = other.behandlingOpprettet;
        this.href = other.href;
        this.aktørId = other.aktørId;
        this.behandlendeEnhet = other.behandlendeEnhet;
        this.behandlingsfrist = other.behandlingsfrist;
        this.fagsakSaksnummer = other.fagsakSaksnummer;
        this.førsteStønadsdag = other.førsteStønadsdag;
        this.behandlingStatus = other.behandlingStatus;
        this.behandlingType = other.behandlingType;
        this.fagsakYtelseType = other.fagsakYtelseType;
        this.system = other.system;
        this.href = other.href;
        this.reservasjon = other.reservasjon;
        this.feilutbetalingstart = other.feilutbetalingstart;
        this.beløp = other.beløp;
    }

    public TilbakekrevingOppgave() {
    }

    public static TilbakekrevingOppgave.Builder tbuilder() {
        return new TilbakekrevingOppgave.Builder();
    }

    public static class Builder {
        private TilbakekrevingOppgave tempOppgave;

        private Builder() {
            tempOppgave = new TilbakekrevingOppgave();
        }

        public TilbakekrevingOppgave.Builder medBeløp(BigDecimal beløp) {
            this.tempOppgave.beløp = beløp;
            return this;
        }

        public TilbakekrevingOppgave.Builder medFeilutbetalingStart(LocalDateTime feilutbetalingStart) {
            this.tempOppgave.feilutbetalingstart = feilutbetalingStart;
            return this;
        }

        public Builder medBehandlingId(BehandlingId behandlingId){
            tempOppgave.behandlingId = behandlingId;
            return this;
        }

        public Builder medFagsakSaksnummer(Long faksagSaksnummer){
            tempOppgave.fagsakSaksnummer = faksagSaksnummer;
            return this;
        }

        public Builder medAktorId(AktørId aktorId){
            tempOppgave.aktørId = aktorId;
            return this;
        }

        public Builder medBehandlendeEnhet(String behandlendeEnhet){
            tempOppgave.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public Builder medAktiv(Boolean aktiv){
            tempOppgave.aktiv = aktiv;
            return this;
        }

        public Builder medBehandlingType(BehandlingType behandlingType){
            tempOppgave.behandlingType = behandlingType;
            return this;
        }

        public Builder medBehandlingStatus(BehandlingStatus behandlingStatus){
            tempOppgave.behandlingStatus = behandlingStatus;
            return this;
        }

        public Builder medSystem(String system){
            tempOppgave.system = system;
            return this;
        }

        public Builder medBehandlingOpprettet(LocalDateTime behandlingOpprettet){
            tempOppgave.behandlingOpprettet = behandlingOpprettet;
            return this;
        }

        public Builder medUtfortFraAdmin(Boolean utfortFraAdmin){
            tempOppgave.utfortFraAdmin = utfortFraAdmin;
            return this;
        }

        public Builder medFagsakYtelseType(FagsakYtelseType fagsakYtelseType){
            tempOppgave.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder medHref(String href){
            tempOppgave.href = href;
            return this;
        }

        public TilbakekrevingOppgave build() {
            return this.tempOppgave;
        }
    }

    @Override
    public String toString() {
        return "TilbakekrevingOppgave{" +
                "beløp=" + beløp +
                ", feilutbetalingstart=" + feilutbetalingstart +
                ", id=" + id +
                ", fagsakSaksnummer=" + fagsakSaksnummer +
                ", aktørId=" + aktørId +
                ", behandlendeEnhet='" + behandlendeEnhet + '\'' +
                ", behandlingsfrist=" + behandlingsfrist +
                ", behandlingOpprettet=" + behandlingOpprettet +
                ", førsteStønadsdag=" + førsteStønadsdag +
                ", behandlingStatus=" + behandlingStatus +
                ", behandlingType=" + behandlingType +
                ", fagsakYtelseType=" + fagsakYtelseType +
                ", aktiv=" + aktiv +
                ", system='" + system + '\'' +
                ", oppgaveAvsluttet=" + oppgaveAvsluttet +
                ", utfortFraAdmin=" + utfortFraAdmin +
                ", behandlingId=" + behandlingId +
                ", href='" + href + '\'' +
                ", reservasjon=" + reservasjon +
                '}';
    }
}
