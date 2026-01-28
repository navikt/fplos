package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

public record SorteringDto(Long fra, Long til, LocalDate fomDato, LocalDate tomDato, KøSortering sorteringType, boolean erDynamiskPeriode, Periodefilter periodefilter) {

    public SorteringDto(OppgaveFiltrering of) {
        this(of.getFra(), of.getTil(), of.getFomDato(), of.getTomDato(), of.getSortering(), of.getPeriodefilter() != Periodefilter.FAST_PERIODE,
            of.getPeriodefilter());
    }
}
