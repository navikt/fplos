package no.nav.foreldrepenger.los.hendelse.hendelseoppretter;

import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;

public class FpsakBehandlingProsessEventDto extends BehandlingProsessEventDto {

    public FpsakBehandlingProsessEventDto() {
    }

    protected FpsakBehandlingProsessEventDto(FpsakBehandlingProsessEventDto.Builder<?> builder) {
        super(builder);
    }

    public static FpsakBehandlingProsessEventDto.Builder<?> builder() {
        return new FpsakBehandlingProsessEventDto.BuilderImpl();
    }

    private static class BuilderImpl extends FpsakBehandlingProsessEventDto.Builder<FpsakBehandlingProsessEventDto.BuilderImpl> {
        private BuilderImpl() {
        }

        protected FpsakBehandlingProsessEventDto.BuilderImpl self() {
            return this;
        }
    }

    public abstract static class Builder<T extends FpsakBehandlingProsessEventDto.Builder<T>> extends no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto.Builder<T> {

        public Builder() {
        }

        public FpsakBehandlingProsessEventDto build() {
            return new FpsakBehandlingProsessEventDto(this);
        }
    }

}
