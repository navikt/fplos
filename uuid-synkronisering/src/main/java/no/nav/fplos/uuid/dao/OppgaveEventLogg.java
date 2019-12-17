package no.nav.fplos.uuid.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "OppgaveEventLogg")
@Table(name = "OPPGAVE_EVENT_LOGG")
public class OppgaveEventLogg extends BaseEntitet {
    @Id
    private Long id;

    @Column(name = "BEHANDLING_ID", nullable = false)
    private Long behandlingId;

    @Column(name = "EKSTERN_ID")
    private UUID eksternId;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getEksternId() {
        return eksternId;
    }

    public Long getId() {
        return id;
    }

    public void setEksternId(UUID eksternId) {
        this.eksternId = eksternId;
    }
}
