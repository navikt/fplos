package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public class OppgaverForAvdelingPerDato {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private LocalDate opprettetDato;
    private Long antall;


    public OppgaverForAvdelingPerDato(Object[] resultat) {
        fagsakYtelseType = FagsakYtelseType.fraKode((String) resultat[0]);  // NOSONAR
        behandlingType = BehandlingType.fraKode((String) resultat[1]);  // NOSONAR
        opprettetDato = ((Date) resultat[2]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // NOSONAR
        antall = ((BigDecimal) resultat[3]).longValue();  // NOSONAR
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public LocalDate getOpprettetDato() {
        return opprettetDato;
    }

    public Long getAntall() {
        return antall;
    }
}
