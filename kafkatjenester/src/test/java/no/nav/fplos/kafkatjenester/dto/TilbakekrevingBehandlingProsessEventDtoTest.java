package no.nav.fplos.kafkatjenester.dto;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TilbakekrevingBehandlingProsessEventDtoTest {

    @Test
    public void testBuilder(){
        TilbakekrevingBehandlingProsessEventDto tilbakekrevingBehandlingProsessEventDto1 = TilbakekrevingBehandlingProsessEventDto.builder().medBeløp(BigDecimal.valueOf(1001.50)).medBehandlingId(1000L).build();
        assertEquals( BigDecimal.valueOf(1001.50), tilbakekrevingBehandlingProsessEventDto1.getBeløp());
        assertEquals(1000L, tilbakekrevingBehandlingProsessEventDto1.getBehandlingId().longValue() );

        TilbakekrevingBehandlingProsessEventDto tilbakekrevingBehandlingProsessEventDto2 = TilbakekrevingBehandlingProsessEventDto.builder().medBehandlingId(2000L).medBeløp(BigDecimal.valueOf(1500.0)).build();
        assertEquals( BigDecimal.valueOf(1500.0), tilbakekrevingBehandlingProsessEventDto2.getBeløp());
        assertEquals(2000L, tilbakekrevingBehandlingProsessEventDto2.getBehandlingId().longValue() );

    }
}
