package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.dto;

import no.nav.vedtak.util.FPDateUtil;

import java.time.LocalDateTime;

public class InnloggetNavAnsattDto {

    private final String brukernavn;
    private final String navn;
    private final boolean kanSaksbehandle;
    private final boolean kanVeilede;
    private final boolean kanBeslutte;
    private final boolean kanBehandleKodeEgenAnsatt;
    private final boolean kanBehandleKode6;
    private final boolean kanBehandleKode7;
    private final LocalDateTime funksjonellTid;
    private final boolean kanOppgavestyre;

    private InnloggetNavAnsattDto(String brukernavn, String navn, boolean kanSaksbehandle, boolean kanVeilede, boolean kanBeslutte, boolean kanBehandleKodeEgenAnsatt, boolean kanBehandleKode6, boolean kanBehandleKode7, boolean kanOppgavestyre) {
        this.brukernavn = brukernavn;
        this.navn = navn;
        this.kanSaksbehandle = kanSaksbehandle;
        this.kanVeilede = kanVeilede;
        this.kanBeslutte = kanBeslutte;
        this.kanBehandleKodeEgenAnsatt = kanBehandleKodeEgenAnsatt;
        this.kanBehandleKode6 = kanBehandleKode6;
        this.kanBehandleKode7 = kanBehandleKode7;
        this.funksjonellTid = LocalDateTime.now(FPDateUtil.getOffset());
        this.kanOppgavestyre = kanOppgavestyre;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public String getNavn() {
        return navn;
    }

    public boolean getKanSaksbehandle() {
        return kanSaksbehandle;
    }

    public boolean getKanVeilede() {
        return kanVeilede;
    }

    public boolean getKanBeslutte() {
        return kanBeslutte;
    }

    public boolean getKanBehandleKodeEgenAnsatt() {
        return kanBehandleKodeEgenAnsatt;
    }

    public boolean getKanBehandleKode6() {
        return kanBehandleKode6;
    }

    public boolean getKanBehandleKode7() {
        return kanBehandleKode7;
    }

    public LocalDateTime getFunksjonellTid() {
        return funksjonellTid;
    }

    public boolean getKanOppgavestyre() {
        return kanOppgavestyre;
    }

    public static class Builder {
        private String brukernavn;
        private String navn;
        private Boolean kanSaksbehandle;
        private Boolean kanVeilede;
        private Boolean kanBeslutte;
        private Boolean kanBehandleKodeEgenAnsatt;
        private Boolean kanBehandleKode6;
        private Boolean kanBehandleKode7;
        private Boolean kanOppgavestyre;

        Builder() {
            kanSaksbehandle = false;
            kanVeilede = false;
            kanBeslutte = false;
            kanBehandleKodeEgenAnsatt = false;
            kanBehandleKode6 = false;
            kanBehandleKode7 = false;
            kanOppgavestyre = false;
        }

        public Builder setBrukernavn(String brukernavn) {
            this.brukernavn = brukernavn;
            return this;
        }

        public Builder setNavn(String navn) {
            this.navn = navn;
            return this;
        }

        public Builder setKanSaksbehandle(Boolean kanSaksbehandle) {
            this.kanSaksbehandle = kanSaksbehandle;
            return this;
        }

        public Builder setKanVeilede(Boolean kanVeilede) {
            this.kanVeilede = kanVeilede;
            return this;
        }

        public Builder setKanBeslutte(Boolean kanBeslutte) {
            this.kanBeslutte = kanBeslutte;
            return this;
        }

        public Builder setKanBehandleKodeEgenAnsatt(Boolean kanBehandleKodeEgenAnsatt) {
            this.kanBehandleKodeEgenAnsatt = kanBehandleKodeEgenAnsatt;
            return this;
        }

        public Builder setKanBehandleKode6(Boolean kanBehandleKode6) {
            this.kanBehandleKode6 = kanBehandleKode6;
            return this;
        }

        public Builder setKanBehandleKode7(Boolean kanBehandleKode7) {
            this.kanBehandleKode7 = kanBehandleKode7;
            return this;
        }

        public Builder setKanOppgavestyre(Boolean kanOppgavestyre){
            this.kanOppgavestyre = kanOppgavestyre;
            return this;
        }

        public InnloggetNavAnsattDto create() {
            return new InnloggetNavAnsattDto(brukernavn, navn, kanSaksbehandle, kanVeilede, kanBeslutte, kanBehandleKodeEgenAnsatt, kanBehandleKode6, kanBehandleKode7, kanOppgavestyre);
        }
    }
}
