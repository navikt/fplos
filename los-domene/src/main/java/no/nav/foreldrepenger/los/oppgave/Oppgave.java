package no.nav.foreldrepenger.los.oppgave;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
@Inheritance(strategy= InheritanceType.JOINED)
public class Oppgave extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE")
    protected Long id;

    @Column(name = "FAGSAK_SAKSNR")
    protected Long fagsakSaksnummer;

    @Column(name = "AKTOR_ID")
    protected Long aktorId;

    @Column(name = "BEHANDLENDE_ENHET")
    protected String behandlendeEnhet;

    @Column(name = "BEHANDLINGSFRIST")
    protected LocalDateTime behandlingsfrist;

    @Column(name = "BEHANDLING_OPPRETTET")
    protected LocalDateTime behandlingOpprettet;

    @Column(name = "FORSTE_STONADSDAG")
    protected LocalDate forsteStonadsdag;

    @Convert(converter = BehandlingStatus.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_STATUS")
    protected BehandlingStatus behandlingStatus;

    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    @Column(name = "BEHANDLING_TYPE")
    protected BehandlingType behandlingType = BehandlingType.INNSYN;

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

    @Column(name = "HREF")
    protected String href;

    @OneToOne(mappedBy = "oppgave")
    protected Reservasjon reservasjon;

    public Long getId() {
        return id;
    }

    public Long getFagsakSaksnummer() {
        return fagsakSaksnummer;
    }

    public AktørId getAktorId() {
        return new AktørId(aktorId);
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

    public Boolean getAktiv() {
        return aktiv;
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

    public LocalDate getForsteStonadsdag() {
        return forsteStonadsdag;
    }

    public BehandlingStatus getBehandlingStatus() {
        return behandlingStatus;
    }

    public LocalDateTime getOppgaveAvsluttet() {
        return oppgaveAvsluttet;
    }

    public Boolean getUtfortFraAdmin() {
        return utfortFraAdmin;
    }

    public Reservasjon getReservasjon() {
        return reservasjon;
    }

    public String getHref() {
        return href;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void deaktiverOppgave() {
        aktiv = false;
    }
    public void avsluttOppgave() {
        aktiv = false;
        oppgaveAvsluttet = LocalDateTime.now();
    }

    public void gjenåpneOppgave() {
        aktiv = true;
        oppgaveAvsluttet = null;
    }

    public void avstemMed(Oppgave other) {
        this.behandlingOpprettet = other.behandlingOpprettet;
        this.href = other.href;
        this.aktorId = other.aktorId;
        this.behandlendeEnhet = other.behandlendeEnhet;
        this.behandlingsfrist = other.behandlingsfrist;
        this.fagsakSaksnummer = other.fagsakSaksnummer;
        this.forsteStonadsdag = other.forsteStonadsdag;
        this.behandlingStatus = other.behandlingStatus;
        this.behandlingType = other.behandlingType;
        this.fagsakYtelseType = other.fagsakYtelseType;
        this.system = other.system;
        this.href = other.href;
    }

    public boolean harAktivReservasjon() {
        return reservasjon != null && reservasjon.erAktiv();
    }

    public static class Builder {
        private Oppgave tempOppgave;

        private Builder(){
            tempOppgave = new Oppgave();
        }

        public Builder medBehandlingId(BehandlingId behandlingId){
            tempOppgave.behandlingId = behandlingId;
            return this;
        }

        public Builder medFagsakSaksnummer(Long faksagSaksnummer){
            tempOppgave.fagsakSaksnummer = faksagSaksnummer;
            return this;
        }

        public Builder medAktorId(Long aktorId){
            tempOppgave.aktorId = aktorId;
            return this;
        }

        public Builder medBehandlendeEnhet(String behandlendeEnhet){
            tempOppgave.behandlendeEnhet = behandlendeEnhet;
            return this;
        }

        public Builder medAktiv(Boolean aktiv){
            tempOppgave.aktiv = aktiv;
            return this;
        }

        public Builder medBehandlingType(BehandlingType behandlingType){
            tempOppgave.behandlingType = behandlingType;
            return this;
        }

        public Builder medSystem(String system){
            tempOppgave.system = system;
            return this;
        }

        public Builder medBehandlingsfrist(LocalDateTime behandlingsfrist){
            tempOppgave.behandlingsfrist = behandlingsfrist;
            return this;
        }

        public Builder medBehandlingOpprettet(LocalDateTime behandlingOpprettet){
            tempOppgave.behandlingOpprettet = behandlingOpprettet;
            return this;
        }

        public Builder medForsteStonadsdag(LocalDate forsteStonadsdag){
            tempOppgave.forsteStonadsdag = forsteStonadsdag;
            return this;
        }
        public Builder medBehandlingStatus(BehandlingStatus behandlingStatus){
            tempOppgave.behandlingStatus = behandlingStatus;
            return this;
        }


        public Builder medOppgaveAvsluttet(LocalDateTime oppgaveAvsluttet){
            tempOppgave.oppgaveAvsluttet = oppgaveAvsluttet;
            return this;
        }

        public Builder medUtfortFraAdmin(Boolean utfortFraAdmin){
            tempOppgave.utfortFraAdmin = utfortFraAdmin;
            return this;
        }

        public Builder medFagsakYtelseType(FagsakYtelseType fagsakYtelseType){
            tempOppgave.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder medHref(String href){
            tempOppgave.href = href;
            return this;
        }

        public Builder dummyOppgave(String enhet){
            tempOppgave.behandlingId = new BehandlingId(UUID.nameUUIDFromBytes("331133L".getBytes()));
            tempOppgave.fagsakSaksnummer = 3478293L;
            tempOppgave.aktorId = 770099L;
            tempOppgave.fagsakYtelseType = FagsakYtelseType.FORELDREPENGER;
            tempOppgave.behandlingType = BehandlingType.FØRSTEGANGSSØKNAD;
            tempOppgave.behandlendeEnhet = enhet;
            tempOppgave.behandlingsfrist = LocalDateTime.now();
            tempOppgave.behandlingOpprettet = LocalDateTime.now();
            tempOppgave.forsteStonadsdag = LocalDate.now().plusMonths(1);
            tempOppgave.behandlingStatus = BehandlingStatus.UTREDES;
            return this;
        }

        public Oppgave build(){
            Oppgave oppgave = tempOppgave;
            tempOppgave = new Oppgave();
            return oppgave;
        }
    }
}
