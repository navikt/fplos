package no.nav.fplos.foreldrepengerbehandling;

import no.nav.fplos.foreldrepengerbehandling.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;

import java.time.LocalDate;
import java.util.List;

public class BehandlingFpsak {

    private static final String MANUELL_MARKERING_AV_UTLAND_SAKSTYPE_AKSJONSPUNKT_KODE = "6068";

    private static final String EØS_BOSATT_NORGE = "EØS_BOSATT_NORGE";
    private static final String BOSATT_UTLAND = "BOSATT_UTLAND";

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
    private List<AksjonspunktDto> aksjonspunkter;
    private Boolean harRefusjonskravFraArbeidsgiver;
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

    public List<AksjonspunktDto> getAksjonspunkter() {
        return aksjonspunkter;
    }

    public Boolean getHarRefusjonskravFraArbeidsgiver() {
        return harRefusjonskravFraArbeidsgiver;
    }

    public Boolean getHarGradering() {
        return harGradering;
    }

    public Boolean getErUtlandssak() {
        return aksjonspunkter.stream()
                .anyMatch(e -> e.getDefinisjon().getKode().equals(MANUELL_MARKERING_AV_UTLAND_SAKSTYPE_AKSJONSPUNKT_KODE)
                        && (e.getBegrunnelse().equals(EØS_BOSATT_NORGE) || e.getBegrunnelse().equals(BOSATT_UTLAND)));
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

        public BehandlingFpsak.Builder medAksjonspunkter(List<AksjonspunktDto> aksjonspunktDtos) {
            behandlingDtoMal.aksjonspunkter = aksjonspunktDtos;
            return this;
        }

        public BehandlingFpsak.Builder medFørsteUttaksdag(LocalDate førsteUttaksdag) {
            behandlingDtoMal.førsteUttaksdag = førsteUttaksdag;
            return this;
        }

        public BehandlingFpsak.Builder medHarRefusjonskrav(Boolean harRefusjonskravFraArbeidsgiver) {
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
