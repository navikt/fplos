package no.nav.foreldrepenger.los.klient.fpsak.dto.inntektarbeidytelse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GraderingPeriodeDto(LocalDate fom, LocalDate tom, BigDecimal arbeidsprosent) {
}
