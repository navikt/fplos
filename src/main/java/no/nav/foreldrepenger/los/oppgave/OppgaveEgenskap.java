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
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

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

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    private Boolean aktiv = Boolean.TRUE;

    public OppgaveEgenskap() {
        //CDI
    }

    public Oppgave getOppgave() {
        return oppgave;
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Oppgave oppgave;
        private AndreKriterierType andreKriterierType;
        private String sisteSaksbehandlerForTotrinn;

        public Builder medOppgave(Oppgave oppgave) {
            this.oppgave = oppgave;
            return this;
        }

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
            if (oppgave == null || andreKriterierType == null) {
                throw new IllegalStateException("Oppgave og/eller AndreKriterierType kan ikke være null");
            }

            if (andreKriterierType.erTilBeslutter() && sisteSaksbehandlerForTotrinn == null) {
                throw new IllegalStateException("Mangler sisteSaksbehandlerForTotrinn for AndreKriterierType " + AndreKriterierType.TIL_BESLUTTER);
            }

            var oppgaveEgenskap = new OppgaveEgenskap();
            oppgaveEgenskap.oppgave = oppgave;
            oppgaveEgenskap.andreKriterierType = andreKriterierType;
            oppgaveEgenskap.sisteSaksbehandlerForTotrinn = sisteSaksbehandlerForTotrinn;
            return oppgaveEgenskap;
        }
    }
}
