package no.nav.foreldrepenger.los.oppgave;

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

import java.util.Objects;

@Entity(name = "OppgaveEgenskap")
@Table(name = "OPPGAVE_EGENSKAP")
public class OppgaveEgenskap extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE_EGENSKAP")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OPPGAVE_ID", nullable = false)
    private Oppgave oppgave;

    @Column(name = "OPPGAVE_ID", updatable = false, insertable = false)
    private Long oppgaveId;

    @Convert(converter = AndreKriterierType.KodeverdiConverter.class)
    @Column(name = "ANDRE_KRITERIER_TYPE", nullable = false)
    private AndreKriterierType andreKriterierType;

    // feltet brukes i query for å ekskludere egne oppgaver i beslutterkøer
    @Column(name = "SISTE_SAKSBEHANDLER_FOR_TOTR")
    private String sisteSaksbehandlerForTotrinn;

    public OppgaveEgenskap() {
        //CDI
    }

    void setOppgave(Oppgave oppgave) {
        this.oppgave = oppgave;
    }

    public Oppgave getOppgave() {
        return oppgave;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OppgaveEgenskap that)) return false;
        return andreKriterierType == that.andreKriterierType && Objects.equals(sisteSaksbehandlerForTotrinn, that.sisteSaksbehandlerForTotrinn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andreKriterierType, sisteSaksbehandlerForTotrinn);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AndreKriterierType andreKriterierType;
        private String sisteSaksbehandlerForTotrinn;

        public Builder medAndreKriterierType(AndreKriterierType andreKriterierType) {
            this.andreKriterierType = andreKriterierType;
            return this;
        }

        public Builder medSisteSaksbehandlerForTotrinn(String sisteSaksbehandler) {
            if (sisteSaksbehandler == null || sisteSaksbehandler.isBlank()) {
                throw new IllegalArgumentException("sisteSaksbehandlerForTotrinn kan ikke være null eller blank");
            }
            this.sisteSaksbehandlerForTotrinn = sisteSaksbehandler.toUpperCase();
            return this;
        }

        public OppgaveEgenskap build() {
            if (andreKriterierType == null) {
                throw new IllegalStateException("AndreKriterierType kan ikke være null");
            }

            if (andreKriterierType.erTilBeslutter() && sisteSaksbehandlerForTotrinn == null) {
                throw new IllegalStateException("Mangler sisteSaksbehandlerForTotrinn for AndreKriterierType " + AndreKriterierType.TIL_BESLUTTER);
            }

            var oppgaveEgenskap = new OppgaveEgenskap();
            oppgaveEgenskap.andreKriterierType = andreKriterierType;
            oppgaveEgenskap.sisteSaksbehandlerForTotrinn = sisteSaksbehandlerForTotrinn;
            return oppgaveEgenskap;
        }
    }
}
