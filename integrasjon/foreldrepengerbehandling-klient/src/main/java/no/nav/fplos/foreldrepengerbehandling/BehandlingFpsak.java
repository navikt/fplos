package no.nav.fplos.foreldrepengerbehandling;

import static no.nav.fplos.foreldrepengerbehandling.Lazy.get;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

public class BehandlingFpsak {
    private BehandlingId behandlingId;
    private String status;
    private String behandlendeEnhetNavn;
    private String ansvarligSaksbehandler;
    private Lazy<List<Aksjonspunkt>> aksjonspunkter;
    private LocalDate behandlingstidFrist;
    private Lazy<LocalDate> førsteUttaksdag;
    private Lazy<Boolean> harRefusjonskravFraArbeidsgiver;
    private Lazy<UttakEgenskaper> uttakEgenskaper;
    private boolean erBerørtBehandling;
    private boolean erEndringssøknad;
    private BehandlingType behandlingType;
    private FagsakYtelseType ytelseType;
    private LocalDateTime behandlingOpprettet;
    private String saksnummer;
    private String aktørId;

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public String getStatus() {
        return status;
    }

    public String getAnsvarligSaksbehandler() {
        return ansvarligSaksbehandler;
    }

    public LocalDateTime getBehandlingOpprettet() {
        return behandlingOpprettet;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getAktørId() {
        return aktørId;
    }

    public Boolean harRefusjonskravFraArbeidsgiver() {
        return get(harRefusjonskravFraArbeidsgiver);
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        var svar = get(aksjonspunkter);
        if (svar == null) {
            return List.of();
        }
        return svar;
    }

    public boolean harGradering() {
        var svar = get(uttakEgenskaper);
        if (svar == null) {
            return false;
        }
        return svar.isGradering();
    }

    public boolean harVurderSykdom() {
        var svar = get(uttakEgenskaper);
        if (svar == null) {
            return false;
        }
        return svar.isVurderSykdom();
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
        return førsteUttaksdag.get();
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

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public void setYtelseType(FagsakYtelseType ytelseType) {
        this.ytelseType = ytelseType;
    }

    public void setSaksnummer(String saksnummer) {
        this.saksnummer = saksnummer;
    }

    public void setAktørId(String aktørId) {
        this.aktørId = aktørId;
    }

    public static final class Builder {
        private BehandlingId behandlingId;
        private String status;
        private String behandlendeEnhetNavn;
        private String ansvarligSaksbehandler;
        private Lazy<List<Aksjonspunkt>> aksjonspunkter;
        private LocalDate behandlingstidFrist;
        private Lazy<LocalDate> førsteUttaksdag;
        private Lazy<Boolean> harRefusjonskravFraArbeidsgiver;
        private boolean erBerørtBehandling = false;
        private boolean erEndringssøknad = false;
        private BehandlingType behandlingType;
        private FagsakYtelseType ytelseType;
        private Lazy<UttakEgenskaper> uttakEgenskaper;
        private LocalDateTime behandlingOpprettet;

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

        public Builder medAksjonspunkter(Lazy<List<Aksjonspunkt>> aksjonspunkter) {
            this.aksjonspunkter = aksjonspunkter;
            return this;
        }

        public Builder medHarRefusjonskravFraArbeidsgiver(Lazy<Boolean> harRefusjonskravFraArbeidsgiver) {
            this.harRefusjonskravFraArbeidsgiver = harRefusjonskravFraArbeidsgiver;
            return this;
        }

        public Builder medUttakEgenskaper(Lazy<UttakEgenskaper> uttakEgenskaper) {
            this.uttakEgenskaper = uttakEgenskaper;
            return this;
        }

        public Builder medBehandlingstidFrist(LocalDate behandlingstidFrist) {
            this.behandlingstidFrist = behandlingstidFrist;
            return this;
        }

        public Builder medFørsteUttaksdag(Lazy<LocalDate> førsteUttaksdag) {
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

        public Builder medBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
            this.behandlingOpprettet = behandlingOpprettet;
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
            behandlingFpsak.uttakEgenskaper = this.uttakEgenskaper;
            behandlingFpsak.behandlingstidFrist = this.behandlingstidFrist;
            behandlingFpsak.behandlingOpprettet = this.behandlingOpprettet;
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
                ", behandlingstidFrist=" + behandlingstidFrist +
                ", erBerørtBehandling=" + erBerørtBehandling +
                ", erEndringssøknad=" + erEndringssøknad +
                ", behandlingType=" + behandlingType +
                ", ytelseType=" + ytelseType +
                '}';
    }
}
