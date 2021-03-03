package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import java.math.BigDecimal;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

public class OppgaverForAvdeling {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private Boolean tilBeslutter;
    private Long antall;


    public OppgaverForAvdeling(Object[] resultat) {
        fagsakYtelseType = FagsakYtelseType.fraKode((String) resultat[0]); // NOSONAR
        behandlingType = BehandlingType.fraKode((String) resultat[1]); // NOSONAR
        tilBeslutter = new BooleanToStringConverter().convertToEntityAttribute((String) resultat[2]); // NOSONAR
        antall = ((BigDecimal)resultat[3]).longValue(); // NOSONAR
    }

    public FagsakYtelseType getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public Boolean getTilBeslutter() {
        return tilBeslutter;
    }

    public Long getAntall() {
        return antall;
    }
}
