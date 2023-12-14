package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record NyeOgFerdigstilteOppgaver(LocalDate dato, BehandlingType behandlingType, Long antallNye, Long antallFerdigstilte) {
}
