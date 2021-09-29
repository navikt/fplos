package no.nav.foreldrepenger.los.statistikk.oppgavebeholdning;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

import java.time.LocalDate;

public record NyeOgFerdigstilteOppgaver(LocalDate dato,
                                        BehandlingType behandlingType,
                                        Long antallNye,
                                        Long antallFerdigstilte) { }
