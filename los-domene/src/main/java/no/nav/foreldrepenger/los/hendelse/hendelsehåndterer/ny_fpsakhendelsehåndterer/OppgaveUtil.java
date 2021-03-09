package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

public class OppgaveUtil {

    private OppgaveUtil() {}

    public static Oppgave oppgave(BehandlingFpsak behandlingFpsak) {
        return Oppgave.builder()
                .medSystem(Fagsystem.FPSAK.name())
                .medFagsakSaksnummer(Long.valueOf(behandlingFpsak.getSaksnummer()))
                .medAktorId(Long.valueOf(behandlingFpsak.getAktørId()))
                .medBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .medBehandlingType(behandlingFpsak.getBehandlingType())
                .medFagsakYtelseType(behandlingFpsak.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(behandlingFpsak.getBehandlingOpprettet())
                .medUtfortFraAdmin(false)
                .medBehandlingStatus(BehandlingStatus.fraKode(behandlingFpsak.getStatus()))
                .medBehandlingId(behandlingFpsak.getBehandlingId())
                .medForsteStonadsdag(behandlingFpsak.getFørsteUttaksdag())
                .medBehandlingsfrist(behandlingFpsak.getBehandlingstidFrist())
                .build();
    }

}
