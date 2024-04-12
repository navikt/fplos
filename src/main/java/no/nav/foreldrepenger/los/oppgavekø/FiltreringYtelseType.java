package no.nav.foreldrepenger.los.oppgavekø;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "FAGSAK_YTELSE_TYPE")
    private FagsakYtelseType fagsakYtelseType;

    public FiltreringYtelseType() {
        //Hibernate
    }

    public FiltreringYtelseType(OppgaveFiltrering oppgaveFiltrering, FagsakYtelseType fagsakYtelseTypeKode) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.fagsakYtelseType = fagsakYtelseTypeKode;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }
}
