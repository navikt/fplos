package no.nav.foreldrepenger.los.oppgavekø;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

@Entity(name = "FiltreringYtelseType")
@Table(name = "FILTRERING_YTELSE_TYPE")
public class FiltreringYtelseType extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTRERING_YTELSE_TYPE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Column(name = "OPPGAVE_FILTRERING_ID", updatable = false, insertable = false)
    private Long oppgaveFiltreringId;

    @Column(name = "FAGSAK_YTELSE_TYPE", updatable = false, insertable = false)
    private String fagsakYtelseTypeKode;

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "FAGSAK_YTELSE_TYPE")
    private FagsakYtelseType fagsakYtelseType;

    public FiltreringYtelseType(){
        //Hibernate
    }

    public FiltreringYtelseType(OppgaveFiltrering oppgaveFiltrering, FagsakYtelseType fagsakYtelseTypeKode) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.fagsakYtelseType = fagsakYtelseTypeKode;
    }

    public OppgaveFiltrering getOppgaveFiltrering() {
        return oppgaveFiltrering;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }
}
