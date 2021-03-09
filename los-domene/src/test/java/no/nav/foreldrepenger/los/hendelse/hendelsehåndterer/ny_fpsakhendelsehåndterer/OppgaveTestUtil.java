package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.Lazy;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OppgaveTestUtil {

    private OppgaveTestUtil() {
    }

    public static BehandlingFpsak behandlingFpsak() {
        var behandlingId = BehandlingId.random();
        var behandlingstidFrist = LocalDate.now().plusDays(10);
        var behandlingOpprettet = LocalDateTime.now();
        var aktørId = AktørId.dummy();
        var behandlingFpsak = BehandlingFpsak.builder()
                .medBehandlingOpprettet(behandlingOpprettet)
                .medBehandlendeEnhetId("4406")
                .medBehandlingId(behandlingId)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFørsteUttaksdag(new Lazy<>(OppgaveTestUtil::førsteUttaksDag))
                .medErBerørtBehandling(false)
                .medErEndringssøknad(false)
                .medBehandlingstidFrist(behandlingstidFrist)
                .medAksjonspunkter(new Lazy<>(OppgaveTestUtil::aksjonspunkter))
                .medStatus("OPPRE")
                .build();
        behandlingFpsak.setSaksnummer("1234");
        behandlingFpsak.setAktørId(aktørId.getId());
        behandlingFpsak.setYtelseType(FagsakYtelseType.FORELDREPENGER);
        return behandlingFpsak;
    }

    public static LocalDate førsteUttaksDag() {
        return LocalDate.of(2021, 3, 1);
    }

    public static List<Aksjonspunkt> aksjonspunkter() {
        var aksjonspunkt = Aksjonspunkt.builder()
                .medDefinisjon("1111")
                .medBegrunnelse("Testbegrunnelse")
                .medFristTid(null)
                .medStatus("OPPR")
                .build();
        return List.of(aksjonspunkt);
    }
}
