package no.nav.fplos.kafkatjenester.genereltgrensesnitt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import no.nav.foreldrepenger.domene.typer.AktørId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class OppgaveEvent {

    private String uuid; //partisjoneres på denne
    private Map<AktørId, AktørRolle> aktoerer;
    private String fagsystem;
    private String fagsystemSaksnummer;
    private String ytelsestype;
    private String behandlingType;
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    @JsonDeserialize(
            using = LocalDateTimeDeserializer.class
    )
    private LocalDateTime hendelseTid; // representerer tidspunkt hendelsen oppstod lokalt for fagsystem
    private boolean aktiverOppgave; //verdi styrer om fplos oppretter/gjenåpner eller lukker oppgave
    private Map attributter;
    private String behandlendeEnhet;
    private String url;

    public String getUuid() {
        return uuid;
    }

    public Map<AktørId, AktørRolle> getAktoerer() {
        return aktoerer;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getFagsystemSaksnummer() {
        return fagsystemSaksnummer;
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

    public boolean isAktiverOppgave() {
        return aktiverOppgave;
    }

    public Map getAttributter() {
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
                ", aktoerer=" + aktoerer +
                ", fagsystem='" + fagsystem + '\'' +
                ", fagsystemSaksnummer='" + fagsystemSaksnummer + '\'' +
                ", ytelsestype='" + ytelsestype + '\'' +
                ", behandlingType='" + behandlingType + '\'' +
                ", hendelseTid=" + hendelseTid +
                ", aktiverOppgave=" + aktiverOppgave +
                ", attributter=" + attributter +
                ", behandlendeEnhet='" + behandlendeEnhet + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    private enum AktørRolle {
        SØKER, ANNEN_AKTØR
    }

    public static final class Builder {
        
        private OppgaveEvent oppgaveEvent = new OppgaveEvent();

        private Builder() {
        }

        public static OppgaveEvent.Builder anOppgaveEvent() {
            return new OppgaveEvent.Builder();
        }

        public OppgaveEvent.Builder withUuid(String uuid) {
            this.oppgaveEvent.uuid = uuid;
            return this;
        }

        public OppgaveEvent.Builder withAktoerId(Map<AktørId, AktørRolle> aktoerId) {
            this.oppgaveEvent.aktoerer = aktoerId;
            return this;
        }

        public OppgaveEvent.Builder withFagsystem(String fagsystem) {
            this.oppgaveEvent.fagsystem = fagsystem;
            return this;
        }

        public OppgaveEvent.Builder withFagsystemSaksnummer(String fagsystemSaksnummer) {
            this.oppgaveEvent.fagsystemSaksnummer = fagsystemSaksnummer;
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

        public OppgaveEvent.Builder withOppgaveAktiveres(boolean oppgaveAktiveres) {
            this.oppgaveEvent.aktiverOppgave = oppgaveAktiveres;
            return this;
        }

        public OppgaveEvent.Builder withAttributter(Map attributter) {
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
            event.aktoerer = this.oppgaveEvent.aktoerer;
            event.url = this.oppgaveEvent.url;
            event.ytelsestype = this.oppgaveEvent.ytelsestype;
            event.fagsystem = this.oppgaveEvent.fagsystem;
            event.hendelseTid = this.oppgaveEvent.hendelseTid;
            event.behandlingType = this.oppgaveEvent.behandlingType;
            event.uuid = this.oppgaveEvent.uuid;
            event.fagsystemSaksnummer = this.oppgaveEvent.fagsystemSaksnummer;
            event.aktiverOppgave = this.oppgaveEvent.aktiverOppgave;
            event.attributter = this.oppgaveEvent.attributter;

            Objects.requireNonNull(event.behandlendeEnhet);
            Objects.requireNonNull(event.aktoerer);
            Objects.requireNonNull(event.ytelsestype);
            Objects.requireNonNull(event.fagsystem);
            Objects.requireNonNull(event.hendelseTid);
            Objects.requireNonNull(event.behandlingType);
            Objects.requireNonNull(event.uuid);
            Objects.requireNonNull(event.fagsystemSaksnummer);
            Objects.requireNonNull(event.aktiverOppgave);
            Objects.requireNonNull(event.attributter);

            return event;
        }
    }
}




/*
 "fagsystem": "fptilbake", // navn på fagsystem
         "fagsystemSaksnummer": "123",
         "uuid": "asdfasfasdfasdf-asdf-asdfas-sdf", // unik id som representerer hele behandlingen.
         "aktoerer": 1234, // aktoerer for person saken gjelder, brukes for å hente navn og tilgangsstyre kode 6/7
         "fagsakYtelsestype": "ES", // ytelsestype
         "behandlendeEnhet": "4415", // eierenhet
         "url": "https://sadfas", // url som tar saksbehandler til korrekt kontekst
         "behandlingType": "FØRSTEGANGSSØKNAD", // førstegangssøknad, klage, anke, revurdering, innsyn
         "hendelseTid": "2018-01-01 12:12:12", // representerer tidspunkt hendelsen oppstod lokalt for fagsystem
         "oppgaveAktiv": true, // verdi styrer om fplos oppretter/gjenåpner eller lukker oppgave
         "attributter": { // inneholder valgfrie attributter, boolean/int/timestamp/text
         "attributt1": true,
         "attributt2": 98398,
         "attributt3": "2019-01-01 00:00:00", // timestamps i attributter normaliseres til dato,
         }*/
