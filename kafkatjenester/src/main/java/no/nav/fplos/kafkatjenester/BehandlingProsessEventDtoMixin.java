package no.nav.fplos.kafkatjenester;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "fagsystem", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TilbakebetalingBehandlingProsessEventDto.class, name = "FPTILBAKE"),
        @JsonSubTypes.Type(value = FpsakBehandlingProsessEventDto.class, name = "FPSAK")
})
public abstract class BehandlingProsessEventDtoMixin {
    public BehandlingProsessEventDtoMixin() {
    }
}
