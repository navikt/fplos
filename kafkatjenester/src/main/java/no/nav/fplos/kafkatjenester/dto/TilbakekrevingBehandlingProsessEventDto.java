package no.nav.fplos.kafkatjenester.dto;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class TilbakekrevingBehandlingProsessEventDto extends BehandlingProsessEventDto {

    private BigDecimal beløp;

    public BigDecimal getBeløp() {
        return beløp;
    }

    public static TilbakekrevingBehandlingProsessEventDto.Builder builder() {
        return new TilbakekrevingBehandlingProsessEventDto.Builder();
    }

    public static class Builder extends BehandlingProsessEventDto.Builder{

        private Builder() {
            behandlingProsessEventDto = new TilbakekrevingBehandlingProsessEventDto();
        }
        public TilbakekrevingBehandlingProsessEventDto.Builder medBeløp(BigDecimal beløp) {
            ((TilbakekrevingBehandlingProsessEventDto)this.behandlingProsessEventDto).beløp = beløp;
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medFagsystem(String fagsystem) {
            super.medFagsystem(fagsystem);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medBehandlingId(Long behandlingId) {
            super.medBehandlingId(behandlingId);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medSaksnummer(String saksnummer) {
            super.medSaksnummer(saksnummer);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medAktørId(String aktørId) {
            super.medAktørId(aktørId);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medEventHendelse(BehandlingProsessEventDto.EventHendelse eventHendelse) {
            super.medEventHendelse(eventHendelse);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medBehandlinStatus(String behandlinStatus) {
            super.medBehandlinStatus(behandlinStatus);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medBehandlingSteg(String behandlingSteg) {
            super.medBehandlingSteg(behandlingSteg);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medBehandlendeEnhet(String behandlendeEnhet) {
            super.medBehandlendeEnhet(behandlendeEnhet);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medYtelseTypeKode(String ytelseTypeKode) {
            super.medYtelseTypeKode(ytelseTypeKode);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medBehandlingTypeKode(String behandlingTypeKode) {
            super.medBehandlingTypeKode(behandlingTypeKode);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medOpprettetBehandling(LocalDateTime opprettetBehandling) {
            super.medOpprettetBehandling(opprettetBehandling);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto.Builder medAksjonspunktKoderMedStatusListe(Map<String, String> aksjonspunktKoderMedStatusListe) {
            super.medAksjonspunktKoderMedStatusListe(aksjonspunktKoderMedStatusListe);
            return this;
        }

        public TilbakekrevingBehandlingProsessEventDto build() {
            return (TilbakekrevingBehandlingProsessEventDto)this.behandlingProsessEventDto;
        }
    }

}
