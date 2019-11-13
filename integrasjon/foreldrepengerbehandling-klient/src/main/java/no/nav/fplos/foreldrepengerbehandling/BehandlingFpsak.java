package no.nav.fplos.foreldrepengerbehandling;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;

import java.time.LocalDate;
import java.util.List;

public class BehandlingFpsak {

    private Long behandlingId;
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
    private boolean harRefusjonskravFraArbeidsgiver;
    private Boolean harGradering;
    private Boolean erUtlandssak;

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

    public boolean getErUtlandssak() {
        return aksjonspunkter.stream()
                .anyMatch(Aksjonspunkt::erUtenlandssak);
    }

    public static BehandlingFpsak.Builder builder() {
        return new BehandlingFpsak.Builder();
    }

    public static class Builder {
        private BehandlingFpsak behandlingDtoMal;

        public Builder() {
            behandlingDtoMal = new BehandlingFpsak();
        }

        public BehandlingFpsak.Builder medBehandlingId(Long behandlingId) {
            behandlingDtoMal.behandlingId = behandlingId;
            return this;
        }

        public BehandlingFpsak.Builder medStatus(String status) {
            behandlingDtoMal.status = status;
            return this;
        }

        public BehandlingFpsak.Builder medBehandlingstidFrist(LocalDate behandlingstidFrist) {
            behandlingDtoMal.behandlingstidFrist = behandlingstidFrist;
            return this;
        }

        public BehandlingFpsak.Builder medType(String type) {
            behandlingDtoMal.type = type;
            return this;
        }

        public BehandlingFpsak.Builder medTema(String tema) {
            behandlingDtoMal.tema = tema;
            return this;
        }

        public BehandlingFpsak.Builder medÅrsak(String årsak) {
            behandlingDtoMal.årsak = årsak;
            return this;
        }

        public BehandlingFpsak.Builder medBehandlendeEnhet(String behandlendeEnhet) {
            behandlingDtoMal.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public BehandlingFpsak.Builder medBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
            behandlingDtoMal.behandlendeEnhetNavn = behandlendeEnhetNavn;
            return this;
        }

        public BehandlingFpsak.Builder medAnsvarligSaksbehandler(String ansvarligSaksbehandler) {
            behandlingDtoMal.ansvarligSaksbehandler = ansvarligSaksbehandler;
            return this;
        }

        public BehandlingFpsak.Builder medAksjonspunkter(List<Aksjonspunkt> aksjonspunktDtos) {
            behandlingDtoMal.aksjonspunkter = aksjonspunktDtos;
            return this;
        }

        public BehandlingFpsak.Builder medFørsteUttaksdag(LocalDate førsteUttaksdag) {
            behandlingDtoMal.førsteUttaksdag = førsteUttaksdag;
            return this;
        }

        public BehandlingFpsak.Builder medHarRefusjonskrav(boolean harRefusjonskravFraArbeidsgiver) {
            behandlingDtoMal.harRefusjonskravFraArbeidsgiver = harRefusjonskravFraArbeidsgiver;
            return this;
        }

        public BehandlingFpsak.Builder medHarGradering(Boolean harGradering) {
            behandlingDtoMal.harGradering = harGradering;
            return this;
        }

        public BehandlingFpsak.Builder medErUtlandssak(Boolean erUtlandssak) {
            behandlingDtoMal.erUtlandssak = erUtlandssak;
            return this;
        }

        public BehandlingFpsak build() {
            return behandlingDtoMal;
        }
    }

    @Override
    public String toString() {
        return "BehandlingFpsak{" +
                "behandlingId=" + behandlingId +
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
                ", erUtlandssak=" + erUtlandssak +
                '}';
    }

}
