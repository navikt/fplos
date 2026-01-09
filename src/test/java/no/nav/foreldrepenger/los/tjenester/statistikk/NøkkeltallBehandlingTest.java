package no.nav.foreldrepenger.los.tjenester.statistikk;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.*;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.NøkkeltallRepository;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.BehandlingVenteStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class NøkkeltallBehandlingTest {

    private NøkkeltallRepository nøkkeltallRepository;
    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        nøkkeltallRepository = new NøkkeltallRepository(entityManager);
        oppgaveRepository = new OppgaveRepository(entityManager);
    }


    @Test
    void hentVentefristTest() {
        assertThat(nøkkeltallRepository.hentVentefristUkefordelt(AVDELING_DRAMMEN_ENHET)).isEmpty();

        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.AKSJONSPUNKT, null);
        lagBehandling(BehandlingType.REVURDERING, BehandlingTilstand.VENT_MANUELL, LocalDateTime.now().plusWeeks(2)); // Ikke førstegang
        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.VENT_KØ, null);
        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.VENT_TIDLIG, LocalDateTime.now().plusWeeks(6));

        var resultater = nøkkeltallRepository.hentVentefristUkefordelt(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(1);
        var resultatet = resultater.getFirst();
        assertThat(resultatet.fagsakYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER);
        assertThat(resultatet.antall()).isEqualTo(1);
    }

    @Test
    void hentFpBehandlingMånedsfordelt() {
        assertThat(nøkkeltallRepository.hentBehandlingMånedsfordeltStønadsdato(AVDELING_DRAMMEN_ENHET)).isEmpty();

        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.AKSJONSPUNKT, null);
        lagBehandling(BehandlingType.REVURDERING, BehandlingTilstand.VENT_MANUELL, LocalDateTime.now().plusWeeks(2)); // Ikke førstegang
        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.VENT_KØ, null);
        lagBehandling(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingTilstand.VENT_TIDLIG, LocalDateTime.now().plusWeeks(6));
        lagBehandling(BehandlingType.KLAGE, BehandlingTilstand.AKSJONSPUNKT, null);
        lagBehandling(BehandlingType.ANKE, BehandlingTilstand.VENT_KLAGEINSTANS, LocalDateTime.now().plusYears(4));

        var resultater = nøkkeltallRepository.hentBehandlingMånedsfordeltStønadsdato(AVDELING_DRAMMEN_ENHET);
        assertThat(resultater).hasSize(5);
        assertThat(resultater.stream().filter(t -> BehandlingType.FØRSTEGANGSSØKNAD.equals(t.behandlingType()))).hasSize(2);
        assertThat(resultater.stream().filter(t -> BehandlingType.REVURDERING.equals(t.behandlingType()))).hasSize(1);
        assertThat(resultater.stream().filter(t -> BehandlingType.KLAGE.equals(t.behandlingType()))).hasSize(1);
        assertThat(resultater.stream().filter(t -> BehandlingType.FØRSTEGANGSSØKNAD.equals(t.behandlingType()))).hasSize(2);
        var resFørste = resultater.stream().filter(t -> BehandlingType.FØRSTEGANGSSØKNAD.equals(t.behandlingType())).toList();
        var resFørsteVent = resFørste.stream().filter(t -> BehandlingVenteStatus.PÅ_VENT.equals(t.behandlingVenteStatus())).findFirst().orElseThrow();
        assertThat(resFørsteVent.antall()).isEqualTo(2);

    }

    private void lagBehandling(BehandlingType type, BehandlingTilstand tilstand, LocalDateTime frist) {
        var behandling = Behandling.builder(Optional.empty())
            .dummyBehandling(AVDELING_DRAMMEN_ENHET, tilstand)
            .medId(UUID.randomUUID())
            .medKildeSystem(Fagsystem.FPSAK)
            .medBehandlingType(type)
            .medVentefrist(frist)
            .build();
        oppgaveRepository.lagreFlushBehandling(behandling);
    }

}
