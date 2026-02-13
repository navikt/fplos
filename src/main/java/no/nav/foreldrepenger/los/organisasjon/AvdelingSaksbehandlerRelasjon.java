package no.nav.foreldrepenger.los.organisasjon;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "AvdelingSaksbehandlerRelasjon")
@IdClass(AvdelingSaksbehandlerNøkkel.class)
@Table(name = "AVDELING_SAKSBEHANDLER")
public class AvdelingSaksbehandlerRelasjon implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAKSBEHANDLER_ID", updatable = false)
    private Saksbehandler saksbehandler;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVDELING_ID", updatable = false)
    private Avdeling avdeling;


    protected AvdelingSaksbehandlerRelasjon() {
        // hibernate
    }

    public AvdelingSaksbehandlerRelasjon(AvdelingSaksbehandlerNøkkel nøkkel) {
        this.saksbehandler = nøkkel.saksbehandler();
        this.avdeling = nøkkel.avdeling();
    }

    public Avdeling getAvdeling() {
        return avdeling;
    }

    public Saksbehandler getSaksbehandler() {
        return saksbehandler;
    }

}
