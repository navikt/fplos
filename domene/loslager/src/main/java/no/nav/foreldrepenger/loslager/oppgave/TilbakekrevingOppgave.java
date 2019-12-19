package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "TilbakekrevingOppgave")
@PrimaryKeyJoinColumn(name = "OPPGAVE_ID")
@Table(name = "TILBAKEKREVING_EGENSKAPER")
public class TilbakekrevingOppgave extends Oppgave{

    @Column(name = "BELOP")
    private BigDecimal belop;

    public BigDecimal getBelop() {
        return belop;
    }

    @Column(name = "UTLOPSFRIST")
    protected LocalDateTime utlopsfrist;

    public LocalDateTime getUtlopsfrist() {
        return utlopsfrist;
    }

    public TilbakekrevingOppgave() {
    }

    public static TilbakekrevingOppgave.Builder tbuilder() {
        return new TilbakekrevingOppgave.Builder();
    }

    public static class Builder {
        private TilbakekrevingOppgave tempOppgave;

        private Builder() {
            tempOppgave = new TilbakekrevingOppgave();
        }

        public TilbakekrevingOppgave.Builder medBelop(BigDecimal belop) {
            ((TilbakekrevingOppgave)this.tempOppgave).belop = belop;
            return this;
        }

        public TilbakekrevingOppgave.Builder medUtlopsfrist(LocalDateTime utlopsfrist) {
            ((TilbakekrevingOppgave)this.tempOppgave).utlopsfrist = utlopsfrist;
            return this;
        }

        public Builder medBehandlingId(Long behandlingId){
            tempOppgave.behandlingId = behandlingId;
            return this;
        }

        public Builder medEksternId(UUID eksternId){
            tempOppgave.eksternId = eksternId;
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

        public TilbakekrevingOppgave build() {
            return (TilbakekrevingOppgave)this.tempOppgave;
        }
    }
}
