package no.nav.fplos.foreldrepengerbehandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.loslager.BehandlingId;

public class BehandlingFpsak {
    private BehandlingId behandlingId;
    private String status;
    private String behandlendeEnhetNavn;
    private String ansvarligSaksbehandler;
    private List<Aksjonspunkt> aksjonspunkter;
    private LocalDate behandlingstidFrist;
    private LocalDate førsteUttaksdag;
    private Boolean harRefusjonskravFraArbeidsgiver;
    private boolean harVurderSykdom;
    private boolean harGradering;
    private boolean erBerørtBehandling;

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public String getStatus() {
        return status;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        return aksjonspunkter;
    }

    public Boolean harRefusjonskravFraArbeidsgiver() {
        return harRefusjonskravFraArbeidsgiver;
    }

    public boolean harGradering() {
        return harGradering;
    }

    public boolean harVurderSykdom() {
        return harVurderSykdom;
    }

    public boolean erBerørtBehandling() {
        return erBerørtBehandling;
    }

    public LocalDateTime getBehandlingstidFrist() {
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }

    public LocalDate getFørsteUttaksdag() {
        return førsteUttaksdag;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BehandlingId behandlingId;
        private String status;
        private String behandlendeEnhetNavn;
        private String ansvarligSaksbehandler;
        private List<Aksjonspunkt> aksjonspunkter;
        private LocalDate behandlingstidFrist;
        private LocalDate førsteUttaksdag;
        private Boolean harRefusjonskravFraArbeidsgiver;
        private boolean harGradering = false;
        private boolean erBerørtBehandling = false;
        private boolean harOverføringPgaSykdom = false;

        private Builder() {
        }

        public Builder medBehandlingId(BehandlingId behandlingId) {
            this.behandlingId = behandlingId;
            return this;
        }

        public Builder medStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            this.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public Builder medAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
            this.ansvarligSaksbehandler = ansvarligSaksbehandler;
            return this;
        }

        public Builder medAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
            this.aksjonspunkter = aksjonspunkter;
            return this;
        }

        public Builder medHarRefusjonskravFraArbeidsgiver(Boolean harRefusjonskravFraArbeidsgiver) {
            this.harRefusjonskravFraArbeidsgiver = harRefusjonskravFraArbeidsgiver;
            return this;
        }

        public Builder medHarGradering(boolean harGradering) {
            this.harGradering = harGradering;
            return this;
        }

        public Builder medHarVurderSykdom(boolean harOverføringPgaSykdom) {
            this.harOverføringPgaSykdom = harOverføringPgaSykdom;
            return this;
        }

        public Builder medBehandlingstidFrist(LocalDate behandlingstidFrist) {
            this.behandlingstidFrist = behandlingstidFrist;
            return this;
        }

        public Builder medFørsteUttaksdag(LocalDate førsteUttaksdag) {
            this.førsteUttaksdag = førsteUttaksdag;
            return this;
        }

        public Builder medErBerørtBehandling(boolean erBerørtBehandling) {
            this.erBerørtBehandling = erBerørtBehandling;
            return this;
        }

        public BehandlingFpsak build() {
            BehandlingFpsak behandlingFpsak = new BehandlingFpsak();
            behandlingFpsak.ansvarligSaksbehandler = this.ansvarligSaksbehandler;
            behandlingFpsak.harRefusjonskravFraArbeidsgiver = this.harRefusjonskravFraArbeidsgiver;
            behandlingFpsak.aksjonspunkter = this.aksjonspunkter;
            behandlingFpsak.status = this.status;
            behandlingFpsak.behandlendeEnhetNavn = this.behandlendeEnhetNavn;
            behandlingFpsak.behandlingId = this.behandlingId;
            behandlingFpsak.harVurderSykdom = this.harOverføringPgaSykdom;
            behandlingFpsak.harGradering = this.harGradering;
            behandlingFpsak.behandlingstidFrist = this.behandlingstidFrist;
            behandlingFpsak.førsteUttaksdag = this.førsteUttaksdag;
            behandlingFpsak.erBerørtBehandling = this.erBerørtBehandling;
            return behandlingFpsak;
        }
    }

    @Override
    public String toString() {
        return "BehandlingFpsak{" +
                "behandlingId=" + behandlingId +
                ", status='" + status + '\'' +
                ", behandlendeEnhetNavn='" + behandlendeEnhetNavn + '\'' +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", aksjonspunkter=" + aksjonspunkter +
                ", harRefusjonskravFraArbeidsgiver=" + harRefusjonskravFraArbeidsgiver +
                ", harGradering=" + harGradering +
                ", harVurderSykdom=" + harVurderSykdom +
                ", behandlingstidFrist=" + behandlingstidFrist +
                ", førsteUttaksdag=" + førsteUttaksdag +
                '}';
    }
}
