package no.nav.foreldrepenger.los.oppgave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.AktørId;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto.LosAksjonspunktDto;

@ExtendWith(JpaExtension.class)
class BehandlingTjenesteTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;
    private BehandlingTjeneste behandlingTjeneste;

    @BeforeEach
    void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepository(entityManager);
        behandlingTjeneste = new BehandlingTjeneste(oppgaveRepository);
    }

    @Test
    void testAksjonspunkterTilAndreKriterierTyperMapping() {
        var cases = new HashMap<String, BehandlingTilstand>();
        cases.put("5082", BehandlingTilstand.AKSJONSPUNKT);
        cases.put("7001", BehandlingTilstand.VENT_MANUELL);
        cases.put("5016", BehandlingTilstand.BESLUTTER);
        cases.put("5012", BehandlingTilstand.PAPIRSØKNAD);

        cases.forEach((k, v) -> {
            var ap = new LosAksjonspunktDto(k, Aksjonspunktstatus.OPPRETTET, null);
            var dto = lagLosBehandlingDto(Kildesystem.FPSAK, List.of(), null, ap);
            behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPSAK);
            var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
            Assertions.assertThat(behandling).isPresent();
            Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(v);
        });
    }

    @Test
    void avbrutt5016GirReturnertFraBeslutterEgenskap() {
        var avbrutt5016 = new LosAksjonspunktDto("5016", Aksjonspunktstatus.AVBRUTT, null);
        var aktivAnnet = new LosAksjonspunktDto("5038", Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPSAK, List.of(), null, avbrutt5016, aktivAnnet);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPSAK);
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.RETUR);
    }

    @Test
    void aktiv5005girTilbakeBeslutter() {
        var aktiv5005 = new LosAksjonspunktDto("5005", Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPTILBAKE, List.of(), null, aktiv5005);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPTILBAKE);
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.BESLUTTER);
    }


    static LosBehandlingDto lagLosBehandlingDto(Kildesystem kildesystem, List<String> sakegenskaper, List<Behandlingsårsak> behandlingsårsaker, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(UUID.randomUUID(), kildesystem, "42", Ytelse.FORELDREPENGER, new AktørId("1234567890123"),
            Behandlingstype.KLAGE, Behandlingsstatus.UTREDES, LocalDateTime.now(), AVDELING_BERGEN_ENHET, LocalDate.now(),
            "z999999", List.of(dto), Optional.ofNullable(behandlingsårsaker).orElse(List.of()), false,
            true, sakegenskaper, new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now()), List.of(), null);
    }

}
