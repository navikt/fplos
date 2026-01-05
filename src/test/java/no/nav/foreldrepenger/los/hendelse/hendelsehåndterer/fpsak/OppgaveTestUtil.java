package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public class OppgaveTestUtil {

    private OppgaveTestUtil() {
    }

    public static LosBehandlingDto behandlingFpsak() {
        return behandlingFpsak(false);
    }

    public static LosBehandlingDto behandlingFpsak(boolean berørt) {
        var behandlingId = BehandlingId.random();
        var behandlingstidFrist = LocalDate.now().plusDays(10);
        var behandlingOpprettet = LocalDateTime.now();
        var aktørId = AktørId.dummy();
        return new LosBehandlingDto(behandlingId.toUUID(), Kildesystem.FPSAK, "1234", Ytelse.FORELDREPENGER,
            new no.nav.vedtak.hendelser.behandling.AktørId(aktørId.getId()), Behandlingstype.FØRSTEGANGS, Behandlingsstatus.OPPRETTET,
            behandlingOpprettet, "4406", behandlingstidFrist, "saksbehandler", OppgaveTestUtil.aksjonspunkter(),
            berørt ? List.of(Behandlingsårsak.BERØRT) : List.of(), false, false, List.of(),
            new LosBehandlingDto.LosForeldrepengerDto(førsteUttaksDag()), List.of(), null);
    }

    public static LocalDate førsteUttaksDag() {
        return LocalDate.of(2021, 3, 1);
    }

    public static List<LosBehandlingDto.LosAksjonspunktDto> aksjonspunkter() {
        return List.of(new LosBehandlingDto.LosAksjonspunktDto("1111", Aksjonspunkttype.AKSJONSPUNKT, Aksjonspunktstatus.OPPRETTET, null));
    }
}
