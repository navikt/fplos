package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public class FpsakBehandlingProsessEventDto extends BehandlingProsessEventDto {

    public FpsakBehandlingProsessEventDto() {
    }

    protected FpsakBehandlingProsessEventDto(FpsakBehandlingProsessEventDto.Builder<?> builder) {
        super(builder);
    }

    private static class BuilderImpl extends FpsakBehandlingProsessEventDto.Builder<FpsakBehandlingProsessEventDto.BuilderImpl> {
        private BuilderImpl() {
        }

        protected FpsakBehandlingProsessEventDto.BuilderImpl self() {
            return this;
        }
    }

    public abstract static class Builder<T extends FpsakBehandlingProsessEventDto.Builder<T>> extends BehandlingProsessEventDto.Builder<T> {

        public FpsakBehandlingProsessEventDto build() {
            return new FpsakBehandlingProsessEventDto(this);
        }
    }

}
