package no.nav.foreldrepenger.los.admin.driftsmelding;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import no.nav.foreldrepenger.los.felles.BaseEntitet;

@Entity(name = "Driftsmelding")
@Table(name = "DRIFTSMELDING")
@Inheritance(strategy = InheritanceType.JOINED)
public class Driftsmelding extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DRIFTSMELDING")
    protected Long id;

    @Column(name = "MELDING")
    protected String melding;

    @Column(name = "AKTIV_FRA")
    protected LocalDateTime aktivFra;

    @Column(name = "AKTIV_TIL")
    protected LocalDateTime aktivTil;

    public Long getId() {
        return id;
    }

    public String getMelding() {
        return melding;
    }

    public LocalDateTime getAktivFra() {
        return aktivFra;
    }

    public LocalDateTime getAktivTil() {
        return aktivTil;
    }

    public boolean erAktiv() {
        var now = LocalDateTime.now();
        return aktivFra.isBefore(now) && aktivTil.isAfter(now);
    }

    public Driftsmelding deaktiver() {
        var now = LocalDateTime.now();
        if (aktivTil.isAfter(now)) {
            aktivTil = now;
        }
        return this;
    }

    public static class Builder {
        private Driftsmelding tempDriftsmelding = new Driftsmelding();

        public static Builder builder() {
            return new Driftsmelding.Builder();
        }

        public Driftsmelding.Builder medMelding(String melding) {
            tempDriftsmelding.melding = melding;
            return this;
        }

        public Driftsmelding.Builder medAktivFra(LocalDateTime aktivFra) {
            tempDriftsmelding.aktivFra = aktivFra;
            return this;
        }

        public Driftsmelding.Builder medAktivTil(LocalDateTime aktivTil) {
            tempDriftsmelding.aktivTil = aktivTil;
            return this;
        }

        public Driftsmelding build() {
            Objects.requireNonNull(tempDriftsmelding.melding, "melding");
            if (tempDriftsmelding.melding.length() == 0) {
                throw new IllegalArgumentException("Melding må ha innhold");
            }
            if (tempDriftsmelding.aktivFra == null) {
                tempDriftsmelding.aktivFra = LocalDateTime.now();
            }
            if (tempDriftsmelding.aktivTil == null) {
                tempDriftsmelding.aktivTil = tempDriftsmelding.aktivFra.plusHours(4);
            }
            var driftsmelding = tempDriftsmelding;
            tempDriftsmelding = new Driftsmelding();
            return driftsmelding;
        }

    }
}
