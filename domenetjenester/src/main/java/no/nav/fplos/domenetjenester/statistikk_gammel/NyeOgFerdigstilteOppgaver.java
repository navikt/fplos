package no.nav.fplos.domenetjenester.statistikk_gammel;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class NyeOgFerdigstilteOppgaver {

    private BehandlingType behandlingType;
    private Long antallNye;
    private Long antallFerdigstilte;
    private LocalDate dato;


    public NyeOgFerdigstilteOppgaver(Object[] resultat) {
        behandlingType = BehandlingType.fraKode((String)resultat[0]); // NOSONAR
        antallNye = ((BigDecimal)resultat[1]).longValue();  // NOSONAR
        antallFerdigstilte = ((BigDecimal)resultat[2]).longValue();  // NOSONAR
        dato = ((Date)resultat[3]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public Long getAntallNye() {
        return antallNye;
    }

    public Long getAntallFerdigstilte() {
        return antallFerdigstilte;
    }

    public LocalDate getDato() {
        return dato;
    }
}
