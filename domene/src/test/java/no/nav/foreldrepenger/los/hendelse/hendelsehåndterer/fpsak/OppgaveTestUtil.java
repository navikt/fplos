package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.Lazy;
import no.nav.foreldrepenger.los.klient.fpsak.dto.ytelsefordeling.RettigheterAnnenForelderDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

public class OppgaveTestUtil {

    private OppgaveTestUtil() {
    }

    public static BehandlingFpsak behandlingFpsak() {
        return behandlingFpsakBuilder().build();
    }

    public static BehandlingFpsak.Builder behandlingFpsakBuilder() {
        var behandlingId = BehandlingId.random();
        var behandlingstidFrist = LocalDate.now().plusDays(10);
        var behandlingOpprettet = LocalDateTime.now();
        var aktørId = AktørId.dummy();
        var behandlingFpsak = BehandlingFpsak.builder()
                .medBehandlingOpprettet(behandlingOpprettet)
                .medBehandlendeEnhetId("4406")
                .medBehandlingId(behandlingId)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medYtelseFordeling(ytelsefordeling())
                .medErBerørtBehandling(false)
                .medErEndringssøknad(false)
                .medBehandlingstidFrist(behandlingstidFrist)
                .medAksjonspunktene(OppgaveTestUtil.aksjonspunkter())
                .medStatus(BehandlingStatus.OPPRETTET)
                .medAnsvarligSaksbehandler("saksbehandler")
                .medAktørId(aktørId)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medSaksnummer(new Saksnummer("1234"));
        return behandlingFpsak;
    }

    private static Lazy<YtelseFordelingDto> ytelsefordeling() {
        return new Lazy<>(() -> new YtelseFordelingDto(førsteUttaksDag(),
                new RettigheterAnnenForelderDto(true)));
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
