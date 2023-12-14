package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto;

import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public record OppgaverForAvdeling(FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType, Boolean tilBehandling, Long antall) {
}
