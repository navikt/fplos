package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;

@Entity(name = "saksbehandlerGruppe")
@Table(name = "SAKSBEHANDLER_GRUPPE")
public class SaksbehandlerGruppe extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRUPPE")
    private Long id;

    @Column(name = "GRUPPE_NAVN")
    private String gruppeNavn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVDELING_ID", updatable = false)
    private Avdeling avdeling;

    public SaksbehandlerGruppe() {
        // hibernate
    }

    public SaksbehandlerGruppe(String gruppeNavn) {
        this.gruppeNavn = gruppeNavn;
    }

    public Long getId() {
        return id;
    }

    public Avdeling getAvdeling() {
        return avdeling;
    }

    public void setGruppeNavn(String gruppeNavn) {
        this.gruppeNavn = gruppeNavn;
    }

    public String getGruppeNavn() {
        return gruppeNavn;
    }

    public void setAvdeling(Avdeling avdeling) {
        this.avdeling = avdeling;
    }
}
