package no.nav.foreldrepenger.loslager.oppgave;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "TilbakekrevingOppgave")
@Table(name = "TILBAKEKREVING_OPPGAVE")
public class TilbakekrevingOppgave extends Oppgave{

    @Column(name = "BELOP")
    private BigDecimal belop;

    public BigDecimal getBelop() {
        return belop;
    }

    public static TilbakekrevingOppgave.Builder builder() {
        return new TilbakekrevingOppgave.Builder();
    }

    public static class Builder extends Oppgave.Builder{

        private Builder() {
            tempOppgave = new TilbakekrevingOppgave();
        }
        public TilbakekrevingOppgave.Builder medBelop(BigDecimal belop) {
            ((TilbakekrevingOppgave)this.tempOppgave).belop = belop;
            return this;
        }

        public TilbakekrevingOppgave.Builder medEksternId(UUID eksternId){
            super.medEksternId(eksternId);
            return this;
        }

        public TilbakekrevingOppgave.Builder medSystem(String fagsystem) {
            super.medSystem(fagsystem);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlingId(Long behandlingId) {
            super.medBehandlingId(behandlingId);
            return this;
        }

        public TilbakekrevingOppgave.Builder medFagsakSaksnummer(Long saksnummer) {
            super.medFagsakSaksnummer(saksnummer);
            return this;
        }

        public TilbakekrevingOppgave.Builder medAktorId(Long aktorId) {
            super.medAktorId(aktorId);
            return this;
        }

        public TilbakekrevingOppgave.Builder medAktiv(Boolean aktiv){
            super.medAktiv(aktiv);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlingStatus(BehandlingStatus behandlingStatus) {
            super.medBehandlingStatus(behandlingStatus);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlendeEnhet(String behandlendeEnhet) {
            super.medBehandlendeEnhet(behandlendeEnhet);
            return this;
        }

        public TilbakekrevingOppgave.Builder medFagsakYtelseType(FagsakYtelseType fagsakYtelseType) {
            super.medFagsakYtelseType(fagsakYtelseType);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlingType(BehandlingType behandlingType) {
            super.medBehandlingType(behandlingType);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlingOpprettet(LocalDateTime behandlingOpprettet) {
            super.medBehandlingOpprettet(behandlingOpprettet);
            return this;
        }

        public TilbakekrevingOppgave.Builder medBehandlingsfrist(LocalDateTime behandlingsfrist){
            super.medBehandlingsfrist(behandlingsfrist);
            return this;
        }

        public TilbakekrevingOppgave.Builder medForsteStonadsdag(LocalDate forsteStonadsdag){
            super.medForsteStonadsdag(forsteStonadsdag);
            return this;
        }

        public TilbakekrevingOppgave.Builder medOppgaveAvsluttet(LocalDateTime oppgaveAvsluttet){
            super.medOppgaveAvsluttet(oppgaveAvsluttet);
            return this;
        }

        public TilbakekrevingOppgave.Builder medUtfortFraAdmin(Boolean utfortFraAdmin){
            super.medUtfortFraAdmin(utfortFraAdmin);
            return this;
        }

        public TilbakekrevingOppgave build() {
            return (TilbakekrevingOppgave)this.tempOppgave;
        }
    }


}
