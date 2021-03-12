package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OppgaverForFørsteStønadsdag {

    private final LocalDate forsteStonadsdag;
    private final Long antall;

    public OppgaverForFørsteStønadsdag(Object[] resultat) {
        this.forsteStonadsdag = ((Date) resultat[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR;
        this.antall = ((BigDecimal) resultat[1]).longValue();  // NOSONAR;
    }

    public LocalDate getForsteStonadsdag() {
        return forsteStonadsdag;
    }

    public Long getAntall() {
        return antall;
    }
}
