package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import no.nav.fplos.kafkatjenester.genereltgrensesnitt.attributt.Attributt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder( {"uuid", "hendelseTid", "aktører", "fagsystem", "saksnummer", "ytelsestype", "behandlingstype", "behandlendeEnhet", "url", "attributter", "saksbehandlereUtenTilgang" } )
public class OppgaveEvent {
    private Uuid uuid; //partisjoneres på denne
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime hendelseTid; //representerer tidspunkt hendelsen oppstod lokalt for produsent
    private List<OppgaveAktør> aktører;
    private Fagsystem fagsystem;
    private String saksnummer;
    private YtelseType ytelsestype;
    private BehandlingsType behandlingsType;
    private String behandlendeEnhet;
    private boolean aktiv; //verdi styrer om fplos oppretter/gjenåpner eller lukker oppgave
    private List<Attributt> attributter;
    private String url;
    private List<OppgaveSaksbehandler> ekskluderSaksbehandlere; //liste over saksbehandlere som ikke har adgang til å behandle oppgaven - gjelder typisk siste saksbehandler før TIL_BESLUTTER

    public Uuid getUuid() {
        return uuid;
    }

    public List<OppgaveAktør> getAktører() {
        return aktører;
    }

    public List<OppgaveSaksbehandler> getEkskluderSaksbehandlere() {
        return ekskluderSaksbehandlere;
    }

    public Fagsystem getFagsystem() {
        return fagsystem;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public YtelseType getYtelsestype() {
        return ytelsestype;
    }

    public BehandlingsType getBehandlingsType() {
        return behandlingsType;
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
                ", saksbehandlerUtenTilgang=" + ekskluderSaksbehandlere + '\'' +
                ", fagsystem='" + fagsystem + '\'' +
                ", saksnummer='" + saksnummer + '\'' +
                ", ytelsestype='" + ytelsestype + '\'' +
                ", behandlingsType='" + behandlingsType + '\'' +
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

        public OppgaveEvent.Builder withUuid(Uuid uuid) {
            this.oppgaveEvent.uuid = uuid;
            return this;
        }

        public OppgaveEvent.Builder withAktoerId(List<OppgaveAktør> aktoerId) {
            this.oppgaveEvent.aktører = aktoerId;
            return this;
        }

        public OppgaveEvent.Builder withFagsystem(Fagsystem fagsystem) {
            this.oppgaveEvent.fagsystem = fagsystem;
            return this;
        }

        public OppgaveEvent.Builder withFagsystemSaksnummer(String fagsystemSaksnummer) {
            this.oppgaveEvent.saksnummer = fagsystemSaksnummer;
            return this;
        }

        public OppgaveEvent.Builder withSaksbehandlereUtenTilgang(List<OppgaveSaksbehandler> saksbehandlere) {
            this.oppgaveEvent.ekskluderSaksbehandlere = saksbehandlere;
            return this;
        }

        public OppgaveEvent.Builder withYtelsestype(YtelseType ytelsestype) {
            this.oppgaveEvent.ytelsestype = ytelsestype;
            return this;
        }

        public OppgaveEvent.Builder withBehandlingType(BehandlingsType behandlingsType) {
            this.oppgaveEvent.behandlingsType = behandlingsType;
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
            event.ekskluderSaksbehandlere = this.oppgaveEvent.ekskluderSaksbehandlere;
            event.aktører = this.oppgaveEvent.aktører;
            event.url = this.oppgaveEvent.url;
            event.ytelsestype = this.oppgaveEvent.ytelsestype;
            event.fagsystem = this.oppgaveEvent.fagsystem;
            event.hendelseTid = this.oppgaveEvent.hendelseTid;
            event.behandlingsType = this.oppgaveEvent.behandlingsType;
            event.uuid = this.oppgaveEvent.uuid;
            event.saksnummer = this.oppgaveEvent.saksnummer;
            event.aktiv = this.oppgaveEvent.aktiv;
            event.attributter = this.oppgaveEvent.attributter;

            Objects.requireNonNull(event.behandlendeEnhet);
            Objects.requireNonNull(event.aktører);
            Objects.requireNonNull(event.ytelsestype);
            Objects.requireNonNull(event.fagsystem);
            Objects.requireNonNull(event.hendelseTid);
            Objects.requireNonNull(event.behandlingsType);
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
                Objects.equals(ekskluderSaksbehandlere, that.ekskluderSaksbehandlere) &&
                Objects.equals(ytelsestype, that.ytelsestype) &&
                Objects.equals(behandlingsType, that.behandlingsType) &&
                Objects.equals(hendelseTid, that.hendelseTid) &&
                Objects.equals(attributter, that.attributter) &&
                Objects.equals(behandlendeEnhet, that.behandlendeEnhet) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, aktører, fagsystem, saksnummer, ekskluderSaksbehandlere,
                ytelsestype, behandlingsType, hendelseTid, aktiv, attributter, behandlendeEnhet, url);
    }

}
