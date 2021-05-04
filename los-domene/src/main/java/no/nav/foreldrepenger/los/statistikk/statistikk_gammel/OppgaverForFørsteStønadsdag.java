package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public record OppgaverForFørsteStønadsdag(LocalDate førsteStonadsdag, Long antall) {

    public OppgaverForFørsteStønadsdag(Object[] resultat) {
        this(((Date) resultat[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                ((BigDecimal) resultat[1]).longValue());
    }
}
