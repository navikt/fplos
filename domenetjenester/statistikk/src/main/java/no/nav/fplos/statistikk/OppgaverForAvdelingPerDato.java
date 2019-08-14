package no.nav.fplos.statistikk;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.kodeverk.KodeverkRepository;

public class OppgaverForAvdelingPerDato {

    private FagsakYtelseType fagsakYtelseType;
    private BehandlingType behandlingType;
    private LocalDate opprettetDato;
    private Long antall;


    public OppgaverForAvdelingPerDato(Object[] resultat, KodeverkRepository kodeverkRepository) {
        fagsakYtelseType = kodeverkRepository.finn(FagsakYtelseType.class, (String) resultat[0]);  // NOSONAR
        behandlingType = kodeverkRepository.finn(BehandlingType.class, (String) resultat[1]);  // NOSONAR
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
