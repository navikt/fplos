package no.nav.fplos.statistikk;

import java.math.BigDecimal;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

public class OppgaverForAvdeling {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private Boolean tilBeslutter;
    private Long antall;


    public OppgaverForAvdeling(Object[] resultat, KodeverkRepository kodeverkRepository) {
        fagsakYtelseType = FagsakYtelseType.fraKode((String) resultat[0]); // NOSONAR
        behandlingType = kodeverkRepository.finn(BehandlingType.class, (String) resultat[1]); // NOSONAR
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
