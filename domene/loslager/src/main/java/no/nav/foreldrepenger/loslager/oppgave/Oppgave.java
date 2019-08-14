package no.nav.foreldrepenger.loslager.oppgave;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.loslager.BaseEntitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity(name = "Oppgave")
@Table(name = "OPPGAVE")
public class Oppgave extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPPGAVE")
    private Long id;

    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "FAGSAK_SAKSNR")
    private Long fagsakSaksnummer;

    @Column(name = "AKTOR_ID")
    private Long aktorId;

    @Column(name = "BEHANDLENDE_ENHET")
    private String behandlendeEnhet;

    @Column(name = "BEHANDLINGSFRIST")
    private LocalDateTime behandlingsfrist;

    @Column(name = "BEHANDLING_OPPRETTET")
    private LocalDateTime behandlingOpprettet;

    @Column(name = "FORSTE_STONADSDAG")
    private LocalDate forsteStonadsdag;

    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "BEHANDLING_STATUS", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + BehandlingStatus.DISCRIMINATOR + "'"))
    private BehandlingStatus behandlingStatus = BehandlingStatus.UDEFINERT;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "behandling_type", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + BehandlingType.DISCRIMINATOR + "'"))
    private BehandlingType behandlingType = BehandlingType.INNSYN;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "FAGSAK_YTELSE_TYPE", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + FagsakYtelseType.DISCRIMINATOR + "'"))
    private FagsakYtelseType fagsakYtelseType = FagsakYtelseType.UDEFINERT;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "AKTIV")
    private Boolean aktiv = Boolean.TRUE;

    @Column(name = "SYSTEM")
    private String system;

    @Column(name = "OPPGAVE_AVSLUTTET")
    private LocalDateTime oppgaveAvsluttet;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "UTFORT_FRA_ADMIN")
    private Boolean utfortFraAdmin = Boolean.FALSE;

    @OneToOne(mappedBy = "oppgave")
    private Reservasjon reservasjon;

    public Long getId() {
        return id;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Long getFagsakSaksnummer() {
        return fagsakSaksnummer;
    }

    public Long getAktorId() {
        return aktorId;
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

    public static Builder builder(){
        return new Builder();
    }

    public void avsluttOppgave() {
        aktiv = false;
        oppgaveAvsluttet = LocalDateTime.now();
    }

    public void gjenåpneOppgave() {
        aktiv = true;
        oppgaveAvsluttet = null;
    }

    public static class Builder {
        private Oppgave tempOppgave;

        private Builder(){
            tempOppgave = new Oppgave();
        }

        public Builder medBehandlingId(Long behandlingId){
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

        public Builder dummyOppgave(String enhet){
            tempOppgave.behandlingId = 331133L;
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
