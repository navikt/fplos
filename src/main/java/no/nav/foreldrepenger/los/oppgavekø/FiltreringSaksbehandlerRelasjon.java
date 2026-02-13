package no.nav.foreldrepenger.los.oppgavekø;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

@Entity(name = "FiltreringSaksbehandlerRelasjon")
@IdClass(FiltreringSaksbehandlerNøkkel.class)
@Table(name = "FILTRERING_SAKSBEHANDLER")
public class FiltreringSaksbehandlerRelasjon implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SAKSBEHANDLER_ID", updatable = false)
    private Saksbehandler saksbehandler;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", updatable = false)
    private OppgaveFiltrering oppgaveFiltrering;


    protected FiltreringSaksbehandlerRelasjon() {
        // hibernate
    }

    public FiltreringSaksbehandlerRelasjon(FiltreringSaksbehandlerNøkkel nøkkel) {
        this.saksbehandler = nøkkel.saksbehandler();
        this.oppgaveFiltrering = nøkkel.oppgaveFiltrering();
    }

    public OppgaveFiltrering getOppgaveFiltrering() {
        return oppgaveFiltrering;
    }

    public Saksbehandler getSaksbehandler() {
        return saksbehandler;
    }

}
