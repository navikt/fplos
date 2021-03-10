package no.nav.foreldrepenger.los.statistikk.statistikk_gammel;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;

import java.time.LocalDate;

public record NyeOgFerdigstilteOppgaver(LocalDate dato,
                                        BehandlingType behandlingType,
                                        Long antallNye,
                                        Long antallFerdigstilte) { }
