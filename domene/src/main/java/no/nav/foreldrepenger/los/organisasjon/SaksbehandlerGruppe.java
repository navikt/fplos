package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.felles.BaseEntitet;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(name = "gruppe_tilknytning", joinColumns = @JoinColumn(name = "gruppe_id"), inverseJoinColumns = @JoinColumn(name = "saksbehandler_id"))
    private Set<Saksbehandler> saksbehandlere = new HashSet<>();

    public SaksbehandlerGruppe() {

    }

    public Long getId() {
        return id;
    }

    public Set<Saksbehandler> getSaksbehandlere() {
        return saksbehandlere;
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
