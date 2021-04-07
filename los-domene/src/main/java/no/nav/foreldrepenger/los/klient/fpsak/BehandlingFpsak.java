package no.nav.foreldrepenger.los.klient.fpsak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.klient.fpsak.dto.Kontrollresultat;
import no.nav.foreldrepenger.los.klient.fpsak.dto.KontrollresultatDto;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public class BehandlingFpsak {
    private BehandlingId behandlingId;
    private String status;
    private String behandlendeEnhetId;
    private String ansvarligSaksbehandler;
    private Lazy<List<Aksjonspunkt>> aksjonspunkter;
    private LocalDate behandlingstidFrist;
    private Lazy<LocalDate> førsteUttaksdag;
    private Lazy<Boolean> harRefusjonskravFraArbeidsgiver;
    private Lazy<UttakEgenskaper> uttakEgenskaper;
    private Lazy<KontrollresultatDto> kontrollresultat;
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
        return Lazy.get(harRefusjonskravFraArbeidsgiver);
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        var svar = Lazy.get(aksjonspunkter);
        if (svar == null) {
            return List.of();
        }
        return svar;
    }

    public boolean harGradering() {
        var svar = Lazy.get(uttakEgenskaper);
        if (svar == null) {
            return false;
        }
        return svar.isGradering();
    }

    public boolean harVurderSykdom() {
        var svar = Lazy.get(uttakEgenskaper);
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

    public String getBehandlendeEnhetId() {
        return behandlendeEnhetId;
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

    public boolean harFareSignaler() {
        var kr = Lazy.get(kontrollresultat);
        if (kr == null) {
            return false;
        }
        return Objects.equals(kr.kontrollresultat(), Kontrollresultat.HOY);
    }

    public static final class Builder {
        private BehandlingId behandlingId;
        private String status;
        private String behandlendeEnhetId;
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
        private Lazy<KontrollresultatDto> kontrollresultat;

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

        public Builder medBehandlendeEnhetId(String behandlendeEnhetId) {
            this.behandlendeEnhetId = behandlendeEnhetId;
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

        public Builder medKontrollresultat(Lazy<KontrollresultatDto> kontrollresultat) {
            this.kontrollresultat = kontrollresultat;
            return this;
        }

        public BehandlingFpsak build() {
            BehandlingFpsak behandlingFpsak = new BehandlingFpsak();
            behandlingFpsak.ansvarligSaksbehandler = this.ansvarligSaksbehandler;
            behandlingFpsak.harRefusjonskravFraArbeidsgiver = this.harRefusjonskravFraArbeidsgiver;
            behandlingFpsak.aksjonspunkter = this.aksjonspunkter;
            behandlingFpsak.status = this.status;
            behandlingFpsak.behandlendeEnhetId = this.behandlendeEnhetId;
            behandlingFpsak.behandlingId = this.behandlingId;
            behandlingFpsak.uttakEgenskaper = this.uttakEgenskaper;
            behandlingFpsak.behandlingstidFrist = this.behandlingstidFrist;
            behandlingFpsak.behandlingOpprettet = this.behandlingOpprettet;
            behandlingFpsak.førsteUttaksdag = this.førsteUttaksdag;
            behandlingFpsak.erBerørtBehandling = this.erBerørtBehandling;
            behandlingFpsak.erEndringssøknad = this.erEndringssøknad;
            behandlingFpsak.behandlingType = this.behandlingType;
            behandlingFpsak.ytelseType = this.ytelseType;
            behandlingFpsak.kontrollresultat = this.kontrollresultat;
            return behandlingFpsak;
        }
    }

    @Override
    public String toString() {
        return "BehandlingFpsak{" +
                "behandlingId=" + behandlingId +
                ", status='" + status + '\'' +
                ", behandlendeEnhetNavn='" + behandlendeEnhetId + '\'' +
                ", ansvarligSaksbehandler='" + ansvarligSaksbehandler + '\'' +
                ", behandlingstidFrist=" + behandlingstidFrist +
                ", erBerørtBehandling=" + erBerørtBehandling +
                ", erEndringssøknad=" + erEndringssøknad +
                ", behandlingType=" + behandlingType +
                ", ytelseType=" + ytelseType +
                ", kontrollresultat=" + kontrollresultat +
                '}';
    }
}
