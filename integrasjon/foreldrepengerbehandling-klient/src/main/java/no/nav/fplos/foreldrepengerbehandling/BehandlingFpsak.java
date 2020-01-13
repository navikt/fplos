package no.nav.fplos.foreldrepengerbehandling;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BehandlingFpsak {
    private Long behandlingId;
    private UUID uuid;
    private String status;
    private LocalDate behandlingstidFrist;
    private String type;
    private String tema;
    private String årsak;
    private String behandlendeEnhet;
    private String behandlendeEnhetNavn;
    private String ansvarligSaksbehandler;
    private LocalDate førsteUttaksdag;
    private List<String> inntektsmeldinger;
    private List<Aksjonspunkt> aksjonspunkter;
    private Boolean erUtenlandssak;
    private Boolean harRefusjonskravFraArbeidsgiver;
    private Boolean harGradering;
    private Boolean harVurderSykdom;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getBehandlingstidFrist() {
        return behandlingstidFrist;
    }

    public String getType() {
        return type;
    }

    public String getTema() {
        return tema;
    }

    public String getÅrsak() {
        return årsak;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public LocalDate getFørsteUttaksdag() {
        return førsteUttaksdag;
    }

    public List<String> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        return aksjonspunkter;
    }

    public Boolean getHarRefusjonskravFraArbeidsgiver() {
        return harRefusjonskravFraArbeidsgiver;
    }

    public Boolean getHarGradering() {
        return harGradering;
    }

    public Boolean getErUtlandssak() {
        return erUtenlandssak;
    }

    public Boolean getHarVurderSykdom() {
        return harVurderSykdom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long behandlingId;
        private UUID uuid;
        private String status;
        private LocalDate behandlingstidFrist;
        private String type;
        private String tema;
        private String årsak;
        private String behandlendeEnhet;
        private String behandlendeEnhetNavn;
        private String ansvarligSaksbehandler;
        private LocalDate førsteUttaksdag;
        private List<String> inntektsmeldinger;
        private List<Aksjonspunkt> aksjonspunkter;
        private Boolean harRefusjonskravFraArbeidsgiver;
        private Boolean harGradering;
        private Boolean harOverføringPgaSykdom;
        private Boolean erUtenlandssak;

        private Builder() {
        }

        public Builder medBehandlingId(Long behandlingId) {
            this.behandlingId = behandlingId;
            return this;
        }

        public Builder medUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder medStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder medBehandlingstidFrist(LocalDate behandlingstidFrist) {
            this.behandlingstidFrist = behandlingstidFrist;
            return this;
        }

        public Builder medType(String type) {
            this.type = type;
            return this;
        }

        public Builder medTema(String tema) {
            this.tema = tema;
            return this;
        }

        public Builder medÅrsak(String årsak) {
            this.årsak = årsak;
            return this;
        }

        public Builder medBehandlendeEnhet(String behandlendeEnhet) {
            this.behandlendeEnhet = behandlendeEnhet;
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

        public Builder medFørsteUttaksdag(LocalDate førsteUttaksdag) {
            this.førsteUttaksdag = førsteUttaksdag;
            return this;
        }

        public Builder medInntektsmeldinger(List<String> inntektsmeldinger) {
            this.inntektsmeldinger = inntektsmeldinger;
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

        public Builder medErUtenlandssak(Boolean erUtenlandssak) {
            this.erUtenlandssak = erUtenlandssak;
            return this;
        }

        public Builder medHarGradering(Boolean harGradering) {
            this.harGradering = harGradering;
            return this;
        }

        public Builder medHarVurderSykdom(Boolean harOverføringPgaSykdom) {
            this.harOverføringPgaSykdom = harOverføringPgaSykdom;
            return this;
        }

        public BehandlingFpsak build() {
            BehandlingFpsak behandlingFpsak = new BehandlingFpsak();
            behandlingFpsak.tema = this.tema;
            behandlingFpsak.førsteUttaksdag = this.førsteUttaksdag;
            behandlingFpsak.behandlingId = this.behandlingId;
            behandlingFpsak.type = this.type;
            behandlingFpsak.ansvarligSaksbehandler = this.ansvarligSaksbehandler;
            behandlingFpsak.harRefusjonskravFraArbeidsgiver = this.harRefusjonskravFraArbeidsgiver;
            behandlingFpsak.aksjonspunkter = this.aksjonspunkter;
            behandlingFpsak.status = this.status;
            behandlingFpsak.årsak = this.årsak;
            behandlingFpsak.inntektsmeldinger = this.inntektsmeldinger;
            behandlingFpsak.behandlendeEnhetNavn = this.behandlendeEnhetNavn;
            behandlingFpsak.uuid = this.uuid;
            behandlingFpsak.harVurderSykdom = this.harOverføringPgaSykdom;
            behandlingFpsak.behandlingstidFrist = this.behandlingstidFrist;
            behandlingFpsak.harGradering = this.harGradering;
            behandlingFpsak.erUtenlandssak = this.erUtenlandssak;
            behandlingFpsak.behandlendeEnhet = this.behandlendeEnhet;
            return behandlingFpsak;
        }
    }

    @Override
    public String toString() {
        return "BehandlingFpsak{" +
                "behandlingId=" + behandlingId +
                ", uuid='" + uuid + '\'' +
                ", status='" + status + '\'' +
                ", behandlingstidFrist=" + behandlingstidFrist +
                ", type='" + type + '\'' +
                ", tema='" + tema + '\'' +
                ", årsak='" + årsak + '\'' +
                ", behandlendeEnhet='" + behandlendeEnhet + '\'' +
                ", behandlendeEnhetNavn='" + behandlendeEnhetNavn + '\'' +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", førsteUttaksdag=" + førsteUttaksdag +
                ", inntektsmeldinger=" + inntektsmeldinger +
                ", aksjonspunkter=" + aksjonspunkter +
                ", harRefusjonskravFraArbeidsgiver=" + harRefusjonskravFraArbeidsgiver +
                ", harOverføringPgaSykdom='" + harVurderSykdom + '\'' +
                ", erUtlandssak=" + erUtenlandssak +
                '}';
    }
}
