package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public class OppgaverForAvdelingSattManueltPaaVent {

    private final FagsakYtelseType fagsakYtelseType;
    private final LocalDate estimertFrist;
    private final Long antall;


    public OppgaverForAvdelingSattManueltPaaVent(Object[] resultat) {
        fagsakYtelseType = FagsakYtelseType.fraKode((String) resultat[1]);  // NOSONAR
        estimertFrist = ((Date) resultat[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR
        antall = ((BigDecimal) resultat[2]).longValue();  // NOSONAR
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public LocalDate getEstimertFrist() {
        return estimertFrist;
    }

    public Long getAntall() {
        return antall;
    }
}
