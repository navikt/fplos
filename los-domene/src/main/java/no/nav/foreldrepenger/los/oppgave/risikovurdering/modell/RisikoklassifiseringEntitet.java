package no.nav.foreldrepenger.los.oppgave.risikovurdering.modell;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "RisikoklassifiseringEntitet")
@Table(name = "RISIKOKLASSIFISERING")
public class RisikoklassifiseringEntitet extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RISIKOKLASSIFISERING")
    private Long id;

    @Column(name = "behandling_id", nullable = false, updatable = false, unique = true)
    private UUID behandlingId;

    @Convert(converter = Kontrollresultat.KodeverdiConverter.class)
    @Column(name = "kontroll_resultat", nullable = false)
    private Kontrollresultat kontrollresultat = Kontrollresultat.UDEFINERT;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "erAktiv", nullable = false)
    private boolean erAktiv = true;

    RisikoklassifiseringEntitet() {
        // Hibernate
    }

    public BehandlingId getBehandlingId() {
        return new BehandlingId(behandlingId);
    }

    public Kontrollresultat getKontrollresultat() {
        return kontrollresultat;
    }

    public boolean erHøyrisiko() {
        return Kontrollresultat.HØY.equals(kontrollresultat);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public boolean isErAktiv() {
        return erAktiv;
    }

    protected void setErAktiv(boolean erAktiv) {
        this.erAktiv = erAktiv;
    }

    public static class Builder {

        private final RisikoklassifiseringEntitet kladd;

        public Builder() {
            this.kladd = new RisikoklassifiseringEntitet();
        }

        public Builder medKontrollresultat(Kontrollresultat kontrollresultat) {
            kladd.kontrollresultat = kontrollresultat;
            return this;
        }

        public RisikoklassifiseringEntitet buildFor(BehandlingId behandlingId) {
            kladd.behandlingId = behandlingId.toUUID();

            Objects.requireNonNull(kladd.behandlingId, "behandlingId");
            return kladd;
        }
    }
}
