package no.nav.fplos.synkronisering.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
public class Oppgave {
    @Id
    private Long id;

    @Column(name = "BEHANDLING_ID")
    protected UUID behandlingId;

    @Column(name = "BEHANDLINGSFRIST")
    protected LocalDateTime behandlingsfrist;

    @Column(name = "FORSTE_STONADSDAG")
    protected LocalDate forsteStonadsdag;

    public Long getId() {
        return id;
    }

    public UUID getBehandlingId() {
        return behandlingId;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public LocalDate getForsteStonadsdag() {
        return forsteStonadsdag;
    }
}
