package no.nav.fplos.statistikk;

import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class OppgaverForAvdelingSattManueltPaaVent {

    private FagsakYtelseType fagsakYtelseType;
    private LocalDate estimertFrist;
    private Long antall;


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
