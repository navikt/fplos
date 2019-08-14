package no.nav.fplos.kafkatjenester.jsonoppgave;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class JsonOppgave{
    private Long behandlingId;
    private Long fagsakId;
    private Long fagsakSaksnummer;
    private String saksbehandlerId;
    private String behandlendeEnhet;
    private String behandlendeEnhetNavn;
    private LocalDateTime reservertTil;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime behandlingsfrist;
    private LocalDateTime behandlingOpprettet;
    private String behandlingType;
    private Boolean aktiv;
    private String system;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public Long getFagsakId() {
        return fagsakId;
    }

    public void setFagsakId(Long fagsakId) {
        this.fagsakId = fagsakId;
    }

    public Long getFagsakSaksnummer() {
        return fagsakSaksnummer;
    }

    public void setFagsakSaksnummer(Long fagsakSaksnummer) {
        this.fagsakSaksnummer = fagsakSaksnummer;
    }

    public String getSaksbehandlerId() {
        return saksbehandlerId;
    }

    public void setSaksbehandlerId(String saksbehandlerId) {
        this.saksbehandlerId = saksbehandlerId;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public void setBehandlendeEnhet(String behandlendeEnhet) {
        this.behandlendeEnhet = behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public void setBehandlendeEnhetNavn(String behandlendeEnhetNavn) {
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
    }

    public LocalDateTime getReservertTil() {
        return reservertTil;
    }

    public void setReservertTil(LocalDateTime reservertTil) {
        this.reservertTil = reservertTil;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public void setBehandlingsfrist(LocalDateTime behandlingsfrist) {
        this.behandlingsfrist = behandlingsfrist;
    }

    public LocalDateTime getBehandlingOpprettet() {
        return behandlingOpprettet;
    }

    public void setBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
        this.behandlingOpprettet = behandlingOpprettet;
    }

    public String getBehandlingType() {
        return behandlingType;
    }

    public void setBehandlingType(String behandlingType) {
        this.behandlingType = behandlingType;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
