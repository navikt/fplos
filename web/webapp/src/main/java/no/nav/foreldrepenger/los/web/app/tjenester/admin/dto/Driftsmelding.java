package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.time.LocalDateTime;

public class Driftsmelding {
    private String id;
    private String melding;
    private boolean aktiv;
    private LocalDateTime opprettet;

    public String getId() {
        return id;
    }

    public String getMelding() {
        return melding;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public static final class DriftsmeldingBuilder {
        private String id;
        private String melding;
        private boolean aktiv;
        private LocalDateTime opprettet;

        private DriftsmeldingBuilder() {
        }

        public static DriftsmeldingBuilder aDriftsmelding() {
            return new DriftsmeldingBuilder();
        }

        public DriftsmeldingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DriftsmeldingBuilder melding(String melding) {
            this.melding = melding;
            return this;
        }

        public DriftsmeldingBuilder erAktiv(boolean aktiv) {
            this.aktiv = aktiv;
            return this;
        }

        public DriftsmeldingBuilder opprettet(LocalDateTime opprettet) {
            this.opprettet = opprettet;
            return this;
        }

        public Driftsmelding build() {
            Driftsmelding driftsmelding = new Driftsmelding();
            driftsmelding.melding = this.melding;
            driftsmelding.opprettet = this.opprettet;
            driftsmelding.id = this.id;
            driftsmelding.aktiv = this.aktiv;
            return driftsmelding;
        }
    }


}
