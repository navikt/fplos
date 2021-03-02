package no.nav.foreldrepenger.loslager.admin;

import no.nav.foreldrepenger.loslager.BaseEntitet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

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
            Driftsmelding driftsmelding = tempDriftsmelding;
            tempDriftsmelding = new Driftsmelding();
            return driftsmelding;
        }

    }
}
