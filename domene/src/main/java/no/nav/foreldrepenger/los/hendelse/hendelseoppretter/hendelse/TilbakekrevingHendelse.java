package no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity(name = "TilbakekrevingHendelse")
@PrimaryKeyJoinColumn(name = "HENDELSE_ID")
@Table(name = "HENDELSE_TILBAKEKREVING")
public class TilbakekrevingHendelse extends Hendelse {

    @Column(name = "feilutbetalt_beløp")
    private BigDecimal feilutbetaltBeløp;

    @Column(name = "href")
    private String href;

    @Column(name = "første_feilutbetaling_dato")
    private LocalDate førsteFeilutbetalingDato;

    @Column(name = "ansvarlig_saksbehandler")
    private String ansvarligSaksbehandler;

    @OneToMany(mappedBy = "hendelse", targetEntity = Aksjonspunkt.class, cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<Aksjonspunkt> aksjonspunkter = new ArrayList<>();

    public TilbakekrevingHendelse() {
    }

    public BigDecimal getFeilutbetaltBeløp() {
        return feilutbetaltBeløp;
    }

    public void setFeilutbetaltBeløp(BigDecimal feilutbetaltBeløp) {
        this.feilutbetaltBeløp = feilutbetaltBeløp;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public LocalDate getFørsteFeilutbetalingDato() {
        return førsteFeilutbetalingDato;
    }

    public void setFørsteFeilutbetalingDato(LocalDate førsteFeilutbetalingDato) {
        this.førsteFeilutbetalingDato = førsteFeilutbetalingDato;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public void setAnsvarligSaksbehandler(String ansvarligSaksbehandlerIdent) {
        this.ansvarligSaksbehandler = ansvarligSaksbehandlerIdent;
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        return aksjonspunkter;
    }

    public void setAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
        for (var aksjonspunkt : aksjonspunkter) {
            aksjonspunkt.setHendelse(this);
        }
        this.aksjonspunkter = aksjonspunkter;
    }
}
