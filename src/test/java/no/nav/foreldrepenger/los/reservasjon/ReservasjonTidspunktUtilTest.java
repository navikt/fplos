package no.nav.foreldrepenger.los.reservasjon;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static no.nav.foreldrepenger.los.reservasjon.ReservasjonTidspunktUtil.JUSTER_TIL_GYLDIG_TIDSPUNKT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservasjonTidspunktUtilTest {

    @Test
    void skalGodtaGyldigeDatoer() {
        for (var dagerFraIdag : new long[]{0, 10, 30}) {
            var date = LocalDate.now().plusDays(dagerFraIdag);
            assertDoesNotThrow(() -> ReservasjonTidspunktUtil.validerReservasjonsdato(date));
        }
    }

    @Test
    void skalFeileForUgyldigeDatoer() {
        for (var dagerFraIdag : new long[]{-1, 31}) {
            var date = LocalDate.now().plusDays(dagerFraIdag);
            assertThrows(IllegalArgumentException.class, () -> ReservasjonTidspunktUtil.validerReservasjonsdato(date));
        }
    }

    @Test
    void skalJustereDatoTilUkedagOgSluttenAvDagen() {
        var søndag = LocalDateTime.of(2026, 3, 15, 15, 43);
        var justertTidspunkt = søndag.with(JUSTER_TIL_GYLDIG_TIDSPUNKT);
        assertThat(justertTidspunkt).isEqualTo(LocalDateTime.of(2026, 3, 16, 23, 59, 59));
    }
}
