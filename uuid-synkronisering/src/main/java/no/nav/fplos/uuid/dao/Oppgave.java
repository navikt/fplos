package no.nav.fplos.uuid.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
public class Oppgave {

    @Id
    private Long id;

    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "EKSTERN_ID")
    private UUID eksternId;

    public Long getId() {
        return id;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getEksternId() {
        return eksternId;
    }

}
