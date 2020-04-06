package no.nav.fplos.foreldrepengerbehandling.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UtsettelseÅrsakTest {

    @Test
    public void skal_mappe_fra_string() {
        assertThat(UtsettelseÅrsak.fraKode("SYKDOM")).isEqualTo(UtsettelseÅrsak.SYKDOM);
        assertThat(UtsettelseÅrsak.fraKode("INSTITUSJONSOPPHOLD_SØKER")).isEqualTo(UtsettelseÅrsak.INSTITUSJONSOPPHOLD_SØKER);
        assertThat(UtsettelseÅrsak.fraKode("INSTITUSJONSOPPHOLD_BARNET")).isEqualTo(UtsettelseÅrsak.INSTITUSJONSOPPHOLD_BARNET);
        assertThat(UtsettelseÅrsak.fraKode("ARBEID")).isEqualTo(UtsettelseÅrsak.ANNET);
        assertThat(UtsettelseÅrsak.fraKode(null)).isEqualTo(null);
        assertThat(UtsettelseÅrsak.fraKode("-")).isEqualTo(null);
    }

}
