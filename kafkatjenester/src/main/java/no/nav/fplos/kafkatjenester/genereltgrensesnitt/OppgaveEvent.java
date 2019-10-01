package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt.Attributt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OppgaveEvent {
    private String uuid; //partisjoneres på denne
    private List<OppgaveAktør> aktører;
    private List<OppgaveSaksbehandler> saksbehandlereUtenTilgang; //liste over saksbehandlere som ikke har adgang til å behandle oppgaven - gjelder typisk siste saksbehandler før TIL_BESLUTTER
    private String fagsystem;
    private String saksnummer;
    private String ytelsestype;
    private String behandlingType;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime hendelseTid; //representerer tidspunkt hendelsen oppstod lokalt for produsent
    private boolean aktiv; //verdi styrer om fplos oppretter/gjenåpner eller lukker oppgave
    private List<Attributt> attributter;
    private String behandlendeEnhet;
    private String url;
    public String getUuid() {
        return uuid;
    }

    public List<OppgaveAktør> getAktører() {
        return aktører;
    }

    public List<OppgaveSaksbehandler> getSaksbehandlereUtenTilgang() {
        return saksbehandlereUtenTilgang;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getYtelsestype() {
        return ytelsestype;
    }

    public String getBehandlingType() {
        return behandlingType;
    }

    public LocalDateTime getHendelseTid() {
        return hendelseTid;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public List<Attributt> getAttributter() {
        return attributter;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "OppgaveEvent{" +
                "uuid='" + uuid + '\'' +
                ", aktører=" + aktører + '\'' +
                ", saksbehandlerUtenTilgang=" + saksbehandlereUtenTilgang + '\'' +
                ", fagsystem='" + fagsystem + '\'' +
                ", saksnummer='" + saksnummer + '\'' +
                ", ytelsestype='" + ytelsestype + '\'' +
                ", behandlingType='" + behandlingType + '\'' +
                ", hendelseTid=" + hendelseTid +
                ", aktiv=" + aktiv +
                ", attributter=" + attributter +
                ", behandlendeEnhet='" + behandlendeEnhet + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public static final class Builder {

        private OppgaveEvent oppgaveEvent = new OppgaveEvent();

        private Builder() {
        }

        public static OppgaveEvent.Builder newBuilder() {
            return new OppgaveEvent.Builder();
        }

        public OppgaveEvent.Builder withUuid(String uuid) {
            this.oppgaveEvent.uuid = uuid;
            return this;
        }

        public OppgaveEvent.Builder withAktoerId(List<OppgaveAktør> aktoerId) {
            this.oppgaveEvent.aktører = aktoerId;
            return this;
        }

        public OppgaveEvent.Builder withFagsystem(String fagsystem) {
            this.oppgaveEvent.fagsystem = fagsystem;
            return this;
        }

        public OppgaveEvent.Builder withFagsystemSaksnummer(String fagsystemSaksnummer) {
            this.oppgaveEvent.saksnummer = fagsystemSaksnummer;
            return this;
        }

        public OppgaveEvent.Builder withSaksbehandlereUtenTilgang(List<OppgaveSaksbehandler> saksbehandlere) {
            this.oppgaveEvent.saksbehandlereUtenTilgang = saksbehandlere;
            return this;
        }

        public OppgaveEvent.Builder withYtelsestype(String ytelsestype) {
            this.oppgaveEvent.ytelsestype = ytelsestype;
            return this;
        }

        public OppgaveEvent.Builder withBehandlingType(String behandlingType) {
            this.oppgaveEvent.behandlingType = behandlingType;
            return this;
        }

        public OppgaveEvent.Builder withHendelseTid(LocalDateTime hendelseTid) {
            this.oppgaveEvent.hendelseTid = hendelseTid;
            return this;
        }

        public OppgaveEvent.Builder withAktiv(boolean oppgaveAktiv) {
            this.oppgaveEvent.aktiv = oppgaveAktiv;
            return this;
        }

        public OppgaveEvent.Builder withAttributter(List<Attributt> attributter) {
            this.oppgaveEvent.attributter = attributter;
            return this;
        }

        public OppgaveEvent.Builder withBehandlendeEnhet(String behandlendeEnhet) {
            this.oppgaveEvent.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public OppgaveEvent.Builder withUrl(String url) {
            this.oppgaveEvent.url = url;
            return this;
        }

        public OppgaveEvent build() {
            OppgaveEvent event = new OppgaveEvent();
            event.behandlendeEnhet = this.oppgaveEvent.behandlendeEnhet;
            event.saksbehandlereUtenTilgang = this.oppgaveEvent.saksbehandlereUtenTilgang;
            event.aktører = this.oppgaveEvent.aktører;
            event.url = this.oppgaveEvent.url;
            event.ytelsestype = this.oppgaveEvent.ytelsestype;
            event.fagsystem = this.oppgaveEvent.fagsystem;
            event.hendelseTid = this.oppgaveEvent.hendelseTid;
            event.behandlingType = this.oppgaveEvent.behandlingType;
            event.uuid = this.oppgaveEvent.uuid;
            event.saksnummer = this.oppgaveEvent.saksnummer;
            event.aktiv = this.oppgaveEvent.aktiv;
            event.attributter = this.oppgaveEvent.attributter;

            Objects.requireNonNull(event.behandlendeEnhet);
            Objects.requireNonNull(event.aktører);
            Objects.requireNonNull(event.ytelsestype);
            Objects.requireNonNull(event.fagsystem);
            Objects.requireNonNull(event.hendelseTid);
            Objects.requireNonNull(event.behandlingType);
            Objects.requireNonNull(event.uuid);
            Objects.requireNonNull(event.saksnummer);

            return event;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OppgaveEvent that = (OppgaveEvent) o;
        return aktiv == that.aktiv &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(aktører, that.aktører) &&
                Objects.equals(fagsystem, that.fagsystem) &&
                Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(saksbehandlereUtenTilgang, that.saksbehandlereUtenTilgang) &&
                Objects.equals(ytelsestype, that.ytelsestype) &&
                Objects.equals(behandlingType, that.behandlingType) &&
                Objects.equals(hendelseTid, that.hendelseTid) &&
                Objects.equals(attributter, that.attributter) &&
                Objects.equals(behandlendeEnhet, that.behandlendeEnhet) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, aktører, fagsystem, saksnummer, saksbehandlereUtenTilgang,
                ytelsestype, behandlingType, hendelseTid, aktiv, attributter, behandlendeEnhet, url);
    }

}
