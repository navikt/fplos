package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

public class OppgaveUtil {

    private OppgaveUtil() {}

    public static Oppgave oppgave(BehandlingFpsak behandlingFpsak) {
        return Oppgave.builder()
                .medSystem(Fagsystem.FPSAK.name())
                .medFagsakSaksnummer(behandlingFpsak.getSaksnummer().longValue())
                .medAktørId(new AktørId(behandlingFpsak.getAktørId()))
                .medBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .medBehandlingType(behandlingFpsak.getBehandlingType())
                .medFagsakYtelseType(behandlingFpsak.getYtelseType())
                .medAktiv(true)
                .medBehandlingOpprettet(behandlingFpsak.getBehandlingOpprettet())
                .medUtfortFraAdmin(false)
                .medBehandlingStatus(behandlingFpsak.getStatus())
                .medBehandlingId(behandlingFpsak.getBehandlingId())
                .medFørsteStønadsdag(behandlingFpsak.getFørsteUttaksdag())
                .medBehandlingsfrist(behandlingFpsak.getBehandlingstidFrist())
                .build();
    }

}
