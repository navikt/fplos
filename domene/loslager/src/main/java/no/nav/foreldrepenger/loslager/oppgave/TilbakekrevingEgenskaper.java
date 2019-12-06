package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity(name = "TilbakekrevingEgenskaper")
@Table(name = "TILBAKEKREVING_EGENSKAPER")
public class TilbakekrevingEgenskaper {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TILBAKEKREVING_EGENSKAPER")
    private Long id;

    @OneToOne
    @JoinColumn(name = "OPPGAVE_ID", nullable = false)
    private Oppgave oppgave;

    @Column(name = "BELOP")
    private BigDecimal belop;

    public BigDecimal getBelop() {
        return belop;
    }

    public TilbakekrevingEgenskaper(Oppgave oppgave, BigDecimal belop) {
        this.oppgave = oppgave;
        this.belop = belop;
    }
/*
    public static TilbakekrevingEgenskaper.Builder builder() {
        return new TilbakekrevingEgenskaper.Builder();
    }

    public static class Builder {
        private TilbakekrevingEgenskaper tempEgenskaper;

        private Builder() {
            tempEgenskaper = new TilbakekrevingEgenskaper();
        }

        public TilbakekrevingEgenskaper.Builder medBelop(BigDecimal belop) {
            this.tempEgenskaper.belop = belop;
            return this;
        }

        public TilbakekrevingEgenskaper build() {
            return this.tempEgenskaper;
        }
    }*/
}
