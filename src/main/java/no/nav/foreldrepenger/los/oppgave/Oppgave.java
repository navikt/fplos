package no.nav.foreldrepenger.los.oppgave;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
@Inheritance(strategy = InheritanceType.JOINED)
public class Oppgave extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE")
    protected Long id;

    @Column(name = "FAGSAK_SAKSNR")
    protected Long fagsakSaksnummer;

    @Embedded
    protected AktørId aktørId;

    @Column(name = "BEHANDLENDE_ENHET")
    protected String behandlendeEnhet;

    @Column(name = "BEHANDLINGSFRIST")
    protected LocalDateTime behandlingsfrist;

    @Column(name = "BEHANDLING_OPPRETTET")
    protected LocalDateTime behandlingOpprettet;

    @Column(name = "FORSTE_STONADSDAG")
    protected LocalDate førsteStønadsdag;

    @Convert(converter = BehandlingStatus.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_STATUS")
    protected BehandlingStatus behandlingStatus;

    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_TYPE")
    protected BehandlingType behandlingType = BehandlingType.INNSYN;

    @OneToMany(mappedBy = "oppgave")
    protected Set<OppgaveEgenskap> oppgaveEgenskaper;

    @Convert(converter = FagsakYtelseType.KodeverdiConverter.class)
    @Column(name = "FAGSAK_YTELSE_TYPE")
    protected FagsakYtelseType fagsakYtelseType;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    protected Boolean aktiv = Boolean.TRUE;

    @Column(name = "SYSTEM")
    protected String system;

    @Column(name = "OPPGAVE_AVSLUTTET")
    protected LocalDateTime oppgaveAvsluttet;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "UTFORT_FRA_ADMIN")
    protected Boolean utfortFraAdmin = Boolean.FALSE;

    @Embedded
    protected BehandlingId behandlingId;

    @OneToOne(mappedBy = "oppgave")
    protected Reservasjon reservasjon;

    public Long getId() {
        return id;
    }

    public Long getFagsakSaksnummer() {
        return fagsakSaksnummer;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public boolean getAktiv() {
        return aktiv != null && aktiv;
    }

    public String getSystem() {
        return system;
    }

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public LocalDateTime getBehandlingsfrist() {
        return behandlingsfrist;
    }

    public LocalDateTime getBehandlingOpprettet() {
        return behandlingOpprettet;
    }

    public LocalDate getFørsteStønadsdag() {
        return førsteStønadsdag;
    }

    public BehandlingStatus getBehandlingStatus() {
        return behandlingStatus;
    }

    public LocalDateTime getOppgaveAvsluttet() {
        return oppgaveAvsluttet;
    }

    public Reservasjon getReservasjon() {
        return reservasjon;
    }

    public Set<OppgaveEgenskap> getOppgaveEgenskaper() {
        return oppgaveEgenskaper == null ? Set.of() : oppgaveEgenskaper;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public void setOppgaveAvsluttet(LocalDateTime avsluttet) {
        this.oppgaveAvsluttet = avsluttet;
    }

    public void avsluttOppgave() {
        aktiv = false;
        oppgaveAvsluttet = LocalDateTime.now();
    }

    public void gjenåpneOppgave() {
        aktiv = true;
        oppgaveAvsluttet = null;
    }

    public boolean harAktivReservasjon() {
        return reservasjon != null && reservasjon.erAktiv();
    }

    @Override
    public String toString() {
        return "Oppgave{" + "id=" + id + ", fagsakSaksnummer=" + fagsakSaksnummer + ", aktiv=" + aktiv + ", system='" + system + '\'' + '}';
    }

    public static class Builder {
        private Oppgave tempOppgave;

        private Builder() {
            tempOppgave = new Oppgave();
        }

        public Builder medBehandlingId(BehandlingId behandlingId) {
            tempOppgave.behandlingId = behandlingId;
            return this;
        }

        public Builder medFagsakSaksnummer(Long faksagSaksnummer) {
            tempOppgave.fagsakSaksnummer = faksagSaksnummer;
            return this;
        }

        public Builder medAktørId(AktørId aktørId) {
            tempOppgave.aktørId = aktørId;
            return this;
        }

        public Builder medBehandlendeEnhet(String behandlendeEnhet) {
            tempOppgave.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public Builder medAktiv(Boolean aktiv) {
            tempOppgave.aktiv = aktiv;
            return this;
        }

        public Builder medBehandlingType(BehandlingType behandlingType) {
            tempOppgave.behandlingType = behandlingType;
            return this;
        }

        public Builder medSystem(String system) {
            tempOppgave.system = system;
            return this;
        }

        public Builder medBehandlingsfrist(LocalDateTime behandlingsfrist) {
            tempOppgave.behandlingsfrist = behandlingsfrist;
            return this;
        }

        public Builder medBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
            tempOppgave.behandlingOpprettet = behandlingOpprettet;
            return this;
        }

        public Builder medFørsteStønadsdag(LocalDate førsteStønadsdag) {
            tempOppgave.førsteStønadsdag = førsteStønadsdag;
            return this;
        }

        public Builder medBehandlingStatus(BehandlingStatus behandlingStatus) {
            tempOppgave.behandlingStatus = behandlingStatus;
            return this;
        }


        public Builder medOppgaveAvsluttet(LocalDateTime oppgaveAvsluttet) {
            tempOppgave.oppgaveAvsluttet = oppgaveAvsluttet;
            return this;
        }

        public Builder medUtfortFraAdmin(Boolean utfortFraAdmin) {
            tempOppgave.utfortFraAdmin = utfortFraAdmin;
            return this;
        }

        public Builder medFagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
            tempOppgave.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder dummyOppgave(String enhet) {
            tempOppgave.behandlingId = new BehandlingId(UUID.nameUUIDFromBytes("331133L".getBytes()));
            tempOppgave.fagsakSaksnummer = 3478293L;
            tempOppgave.aktørId = AktørId.dummy();
            tempOppgave.fagsakYtelseType = FagsakYtelseType.FORELDREPENGER;
            tempOppgave.behandlingType = BehandlingType.FØRSTEGANGSSØKNAD;
            tempOppgave.behandlendeEnhet = enhet;
            tempOppgave.behandlingsfrist = LocalDateTime.now();
            tempOppgave.behandlingOpprettet = LocalDateTime.now();
            tempOppgave.førsteStønadsdag = LocalDate.now().plusMonths(1);
            tempOppgave.behandlingStatus = BehandlingStatus.UTREDES;
            return this;
        }

        public Oppgave build() {
            var oppgave = tempOppgave;
            tempOppgave = new Oppgave();
            return oppgave;
        }
    }
}
