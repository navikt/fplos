package no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "TilbakekrevingAksjonspunkt")
@Table(name = "HENDELSE_TK_AKSJONSPUNKT")
public class Aksjonspunkt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HENDELSE_TK_AKSJONSPUNKT")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tilbakekreving_hendelse_id", nullable = false, updatable = false)
    private TilbakekrevingHendelse hendelse;

    @Column(name = "kode", nullable = false)
    private String kode;

    @Column(name = "status", nullable = false)
    private String status;

    public Aksjonspunkt(String kode, String status) {
        this.kode = kode;
        this.status = status;
    }

    public Aksjonspunkt() {
        //hibernate
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean erOpprettet() {
        return getStatus().equals("OPPR");
    }

    public void setHendelse(TilbakekrevingHendelse hendelse) {
        this.hendelse = hendelse;
    }
}
