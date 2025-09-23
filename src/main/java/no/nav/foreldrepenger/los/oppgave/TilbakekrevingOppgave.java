package no.nav.foreldrepenger.los.oppgave;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;


@Entity(name = "TilbakekrevingOppgave")
@PrimaryKeyJoinColumn(name = "OPPGAVE_ID")
@Table(name = "TILBAKEKREVING_EGENSKAPER")
public class TilbakekrevingOppgave extends Oppgave {

    @Column(name = "BELOP")
    private BigDecimal belop;

    @Column(name = "FEILUTBETALINGSTART")
    protected LocalDateTime feilutbetalingstart;

    public BigDecimal getBelop() {
        return belop;
    }

    public LocalDateTime getFeilutbetalingstart() {
        return feilutbetalingstart;
    }

    public void setBelop(BigDecimal beløp) {
        this.belop = beløp;
    }

    public void setFeilutbetalingstart(LocalDateTime feilutbetalingstart) {
        this.feilutbetalingstart = feilutbetalingstart;
    }

    public void avstemMed(TilbakekrevingOppgave other) {
        this.behandlingOpprettet = other.behandlingOpprettet;
        this.aktørId = other.aktørId;
        this.behandlendeEnhet = other.behandlendeEnhet;
        this.behandlingsfrist = other.behandlingsfrist;
        this.saksnummer = other.saksnummer;
        this.førsteStønadsdag = other.førsteStønadsdag;
        this.behandlingType = other.behandlingType;
        this.fagsakYtelseType = other.fagsakYtelseType;
        this.system = other.system;
        this.reservasjon = other.reservasjon;
        this.feilutbetalingstart = other.feilutbetalingstart;
        this.belop = other.belop;
        this.feilutbetalingStart = other.feilutbetalingstart; // expand: kopierer over til super
        this.feilutbetalingBelop = other.belop; // expand: kopierer over til super
    }

    public TilbakekrevingOppgave() {
        // Hibernate
    }

    public static TilbakekrevingOppgave.TbkBuilder tbuilder() {
        return new TilbakekrevingOppgave.TbkBuilder();
    }

    public static class TbkBuilder extends Oppgave.Builder<TbkBuilder> {
        private TbkBuilder() {
            this.tempOppgave = new TilbakekrevingOppgave();
        }

        @Override
        protected TbkBuilder self() {
            return this;
        }

        @Override
        public TilbakekrevingOppgave build() {
            return (TilbakekrevingOppgave) tempOppgave;
        }

        public TbkBuilder medBeløp(BigDecimal beløp) {
            ((TilbakekrevingOppgave) this.tempOppgave).belop = beløp;
            this.tempOppgave.feilutbetalingBelop = beløp;
            return this;
        }

        @Override
        public TbkBuilder medFeilutbetalingStart(LocalDateTime feilutbetalingStart) {
            ((TilbakekrevingOppgave) this.tempOppgave).feilutbetalingstart = feilutbetalingStart;
            this.tempOppgave.feilutbetalingStart = feilutbetalingStart;
            return this;
        }
    }



}
