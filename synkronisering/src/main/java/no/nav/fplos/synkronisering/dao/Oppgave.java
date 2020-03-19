package no.nav.fplos.synkronisering.dao;

import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
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

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    protected Boolean aktiv = Boolean.TRUE;

    @Column(name = "SYSTEM")
    protected String system;

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

    public Boolean getAktiv() {
        return aktiv;
    }

    public String getSystem() {
        return system;
    }
}
