package no.nav.fplos.foreldrepengerbehandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

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
    private boolean erEndringssøknad;
    private BehandlingType behandlingType;
    private FagsakYtelseType ytelseType;

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
        return Optional.ofNullable(aksjonspunkter).orElse(Collections.emptyList());
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

    public boolean erEndringssøknad() {
        return erEndringssøknad;
    }

    public LocalDateTime getBehandlingstidFrist() {
        return behandlingstidFrist != null ? behandlingstidFrist.atStartOfDay() : null;
    }

    public LocalDate getFørsteUttaksdag() {
        return førsteUttaksdag;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public FagsakYtelseType getYtelseType() {
        return ytelseType;
    }

    public void setYtelseType(FagsakYtelseType ytelseType) {
        this.ytelseType = ytelseType;
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
        private boolean erEndringssøknad = false;
        private BehandlingType behandlingType;
        private FagsakYtelseType ytelseType;

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

        public Builder medErEndringssøknad(boolean erEndringssøknad) {
            this.erEndringssøknad = erEndringssøknad;
            return this;
        }

        public Builder medBehandlingType(BehandlingType type) {
            this.behandlingType = type;
            return this;
        }

        public Builder medYtelseType(FagsakYtelseType ytelseType) {
            this.ytelseType = ytelseType;
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
            behandlingFpsak.erEndringssøknad = this.erEndringssøknad;
            behandlingFpsak.behandlingType = this.behandlingType;
            behandlingFpsak.ytelseType = this.ytelseType;
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
                ", behandlingstidFrist=" + behandlingstidFrist +
                ", førsteUttaksdag=" + førsteUttaksdag +
                ", harRefusjonskravFraArbeidsgiver=" + harRefusjonskravFraArbeidsgiver +
                ", harVurderSykdom=" + harVurderSykdom +
                ", harGradering=" + harGradering +
                ", erBerørtBehandling=" + erBerørtBehandling +
                ", erEndringssøknad=" + erEndringssøknad +
                ", behandlingType=" + behandlingType +
                ", ytelseType=" + ytelseType +
                '}';
    }
}
