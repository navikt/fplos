package no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

import java.time.LocalDate;

public record SorteringDto(Long fra, Long til, LocalDate fomDato, LocalDate tomDato, KøSortering sorteringType, boolean erDynamiskPeriode) {

    public SorteringDto(OppgaveFiltrering of) {
        this(of.getFra(), of.getTil(), of.getFomDato(), of.getTomDato(), of.getSortering(), of.getErDynamiskPeriode());
    }
}
