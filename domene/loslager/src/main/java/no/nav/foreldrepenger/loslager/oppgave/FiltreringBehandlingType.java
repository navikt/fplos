package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.loslager.BaseEntitet;

@Entity(name = "FiltreringBehandlingType")
@Table(name = "FILTRERING_BEHANDLING_TYPE")
public class FiltreringBehandlingType extends BaseEntitet{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FILTR_BEHANDLING_TYPE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_FILTRERING_ID", nullable = false)
    private OppgaveFiltrering oppgaveFiltrering;

    @Column(name = "OPPGAVE_FILTRERING_ID", updatable = false, insertable = false)
    private Long oppgaveFiltreringId;

    @Column(name = "behandling_type", updatable = false, insertable = false)
    private String behandlingTypeKode;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "behandling_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + BehandlingType.DISCRIMINATOR + "'"))
    private BehandlingType behandlingType;

    public FiltreringBehandlingType(){
        //CDI
    }

    public FiltreringBehandlingType(OppgaveFiltrering oppgaveFiltrering, BehandlingType behandlingType) {
        this.oppgaveFiltrering = oppgaveFiltrering;
        this.behandlingType = behandlingType;
    }

    public OppgaveFiltrering getOppgaveFiltrering() {
        return oppgaveFiltrering;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }
}
