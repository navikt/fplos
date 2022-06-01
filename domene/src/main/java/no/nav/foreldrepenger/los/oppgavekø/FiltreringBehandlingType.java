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
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

@Entity(name = "FiltreringBehandlingType")
@Table(name = "FILTRERING_BEHANDLING_TYPE")
public class FiltreringBehandlingType extends BaseEntitet {
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

    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_TYPE")
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
