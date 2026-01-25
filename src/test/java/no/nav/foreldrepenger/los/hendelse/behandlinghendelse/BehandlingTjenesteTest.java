package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.AktørId;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Behandlingsårsak;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto.LosAksjonspunktDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

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
    void testAksjonspunkterTilTilstand() {
        Map<String, BehandlingTilstand> cases = Map.of(
            "5082", BehandlingTilstand.AKSJONSPUNKT,
            "7001", BehandlingTilstand.VENT_MANUELL,
            "5016", BehandlingTilstand.BESLUTTER,
            "5012", BehandlingTilstand.PAPIRSØKNAD);
        Map<String, Aksjonspunkttype> typer = Map.of(
            "5082", Aksjonspunkttype.AKSJONSPUNKT,
            "7001", Aksjonspunkttype.VENT,
            "5016", Aksjonspunkttype.BESLUTTER,
            "5012", Aksjonspunkttype.PAPIRSØKNAD);
        Map<String, Set<AndreKriterierType>> kriterier = Map.of(
            "5082", Set.of(),
            "7001", Set.of(),
            "5016", EnumSet.of(AndreKriterierType.TIL_BESLUTTER),
            "5012", EnumSet.of(AndreKriterierType.PAPIRSØKNAD));

        cases.forEach((k, v) -> {
            var ap = new LosAksjonspunktDto(k, typer.get(k), Aksjonspunktstatus.OPPRETTET, null);
            var dto = lagLosBehandlingDto(Kildesystem.FPSAK, List.of(), ap);
            behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPSAK, Optional.empty(), kriterier.get(k));
            var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
            Assertions.assertThat(behandling).isPresent();
            Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(v);
            var egenskaper = oppgaveRepository.finnBehandlingKriterier(dto.behandlingUuid());
            Assertions.assertThat(egenskaper).hasSameSizeAs(kriterier.get(k));
            Assertions.assertThat(egenskaper).containsExactlyInAnyOrderElementsOf(kriterier.get(k));
        });
    }

    @Test
    void avbrutt5016GirAksjonspunkt() {
        var avbrutt5016 = new LosAksjonspunktDto("5016", Aksjonspunkttype.BESLUTTER, Aksjonspunktstatus.AVBRUTT, null);
        var aktivAnnet = new LosAksjonspunktDto("5038", Aksjonspunkttype.AKSJONSPUNKT, Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPSAK, List.of("NÆRING"), avbrutt5016, aktivAnnet);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPSAK, Optional.empty(), Set.of());
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.AKSJONSPUNKT);
    }

    @Test
    void aktiv7003girTilbakeAksjonspunkt() {
        var aktiv7003 = new LosAksjonspunktDto("7003", Aksjonspunkttype.AKSJONSPUNKT, Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPTILBAKE, List.of(), aktiv7003);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPTILBAKE, Optional.empty(), Set.of());
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.AKSJONSPUNKT);
    }

    @Test
    void aktiv7001girTilbakeVent() {
        var aktiv5005 = new LosAksjonspunktDto("7001", Aksjonspunkttype.VENT, Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPTILBAKE, List.of(), aktiv5005);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPTILBAKE, Optional.empty(), Set.of());
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.VENT_MANUELL);
    }

    @Test
    void aktiv5005girTilbakeBeslutter() {
        var aktiv5005 = new LosAksjonspunktDto("5005", Aksjonspunkttype.BESLUTTER, Aksjonspunktstatus.OPPRETTET, null);
        var dto = lagLosBehandlingDto(Kildesystem.FPTILBAKE, List.of(), aktiv5005);
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPTILBAKE, Optional.empty(), Set.of(AndreKriterierType.TIL_BESLUTTER));
        var behandling = oppgaveRepository.finnBehandling(dto.behandlingUuid());
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(BehandlingTilstand.BESLUTTER);
    }


    static LosBehandlingDto lagLosBehandlingDto(Kildesystem kildesystem, List<String> sakegenskaper, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(UUID.randomUUID(), kildesystem, "42", Ytelse.FORELDREPENGER, new AktørId("1234567890123"),
            Behandlingstype.KLAGE, Behandlingsstatus.UTREDES, LocalDateTime.now(), AVDELING_BERGEN_ENHET, LocalDate.now(),
            "z999999", List.of(dto), Optional.ofNullable((List<Behandlingsårsak>) null).orElse(List.of()), false,
            true, sakegenskaper, new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now()), List.of(), null);
    }

    @Test
    void sekvens_fpsak_endrer_behandling_egenskaper() {
        var aktiv5016 = new LosAksjonspunktDto("5016", Aksjonspunkttype.BESLUTTER, Aksjonspunktstatus.OPPRETTET, null);
        var avbrutt5016 = new LosAksjonspunktDto("5016", Aksjonspunkttype.BESLUTTER, Aksjonspunktstatus.AVBRUTT, null);
        var utført5016 = new LosAksjonspunktDto("5016", Aksjonspunkttype.BESLUTTER, Aksjonspunktstatus.UTFØRT, null);
        var aktivAnnet = new LosAksjonspunktDto("5039", Aksjonspunkttype.AKSJONSPUNKT, Aksjonspunktstatus.OPPRETTET, null);
        var utførtAnnet = new LosAksjonspunktDto("5039", Aksjonspunkttype.AKSJONSPUNKT, Aksjonspunktstatus.UTFØRT, null);
        var aktivPapir = new LosAksjonspunktDto("5040", Aksjonspunkttype.PAPIRSØKNAD, Aksjonspunktstatus.OPPRETTET, null);
        var utførtPapir = new LosAksjonspunktDto("5040", Aksjonspunkttype.PAPIRSØKNAD, Aksjonspunktstatus.UTFØRT, null);
        var aktivKomplett = new LosAksjonspunktDto("7003", Aksjonspunkttype.VENT, Aksjonspunktstatus.OPPRETTET, null);
        var utførtKomplett = new LosAksjonspunktDto("7003", Aksjonspunkttype.VENT, Aksjonspunktstatus.UTFØRT, null);
        var aktivManuell = new LosAksjonspunktDto("7001", Aksjonspunkttype.VENT, Aksjonspunktstatus.OPPRETTET, null);
        var utførtManuell = new LosAksjonspunktDto("7001", Aksjonspunkttype.VENT, Aksjonspunktstatus.UTFØRT, null);
        var behandlingUuid = UUID.randomUUID();

        // Papirsøknad
        var dtoPapir = lagSekvensDto(behandlingUuid, aktivPapir);
        utledKriterieLagreBehandling(dtoPapir, List.of(), Optional.empty());
        var forventetPapir = EnumSet.of(AndreKriterierType.PAPIRSØKNAD);
        var behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.PAPIRSØKNAD, forventetPapir);

        // Vent kompletthet
        var dtoKomplett = lagSekvensDto(behandlingUuid, utførtPapir, aktivKomplett);
        utledKriterieLagreBehandling(dtoKomplett, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritKomplett = EnumSet.of(AndreKriterierType.BARE_FAR_RETT);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.VENT_KOMPLETT, kritKomplett);

        // Aksjonspunkt beregning
        var dtoBeregning1 = lagSekvensDto(behandlingUuid, utførtPapir, utførtKomplett, aktivAnnet);
        utledKriterieLagreBehandling(dtoBeregning1, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritBeregning1 = EnumSet.of(AndreKriterierType.BARE_FAR_RETT, AndreKriterierType.NÆRING);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.AKSJONSPUNKT, kritBeregning1);

        // Aksjonspunkt beslutter 1
        var dtoBeslutter1 = lagSekvensDto(behandlingUuid, utførtPapir, utførtKomplett, utførtAnnet, aktiv5016);
        utledKriterieLagreBehandling(dtoBeslutter1, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritBeslutter1 = EnumSet.of(AndreKriterierType.BARE_FAR_RETT, AndreKriterierType.TIL_BESLUTTER);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.BESLUTTER, kritBeslutter1);

        // Aksjonspunkt retur
        var dtoBeregning2 = lagSekvensDto(behandlingUuid, utførtPapir, utførtKomplett, avbrutt5016, aktivAnnet);
        utledKriterieLagreBehandling(dtoBeregning2, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritBeregning2 = EnumSet.of(AndreKriterierType.BARE_FAR_RETT, AndreKriterierType.NÆRING, AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.AKSJONSPUNKT, kritBeregning2);

        // Satte på vent manuell
        var dtoManuell = lagSekvensDto(behandlingUuid, utførtPapir, utførtKomplett, avbrutt5016, aktivAnnet, aktivManuell);
        utledKriterieLagreBehandling(dtoManuell, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritManuell = EnumSet.of(AndreKriterierType.BARE_FAR_RETT, AndreKriterierType.NÆRING, AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.VENT_MANUELL, kritManuell);

        // Aksjonspunkt beslutter 2
        var dtoBeslutter2 = lagSekvensDto(behandlingUuid, utførtPapir, utførtKomplett, utførtAnnet, utførtManuell, aktiv5016);
        utledKriterieLagreBehandling(dtoBeslutter2, List.of("HASTER", "BARE_FAR_RETT", "NÆRING"), behandling);
        var kritBeslutter2 = EnumSet.of(AndreKriterierType.BARE_FAR_RETT, AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.HASTER);
        behandling = finnAssertBehandling(behandlingUuid, BehandlingTilstand.BESLUTTER, kritBeslutter2 );

        // Avsluttet
        var dtoAvsluttet = lagSekvensDtoAvsluttet(behandlingUuid, utførtPapir, utførtKomplett, utførtAnnet, utførtManuell, utført5016);
        utledKriterieLagreBehandling(dtoAvsluttet, List.of("BARE_FAR_RETT", "NÆRING"), behandling);
        var kritAvsluttet = EnumSet.of(AndreKriterierType.BARE_FAR_RETT);
        finnAssertBehandling(behandlingUuid, BehandlingTilstand.AVSLUTTET, kritAvsluttet);
    }

    private void utledKriterieLagreBehandling(LosBehandlingDto dto, List<String> saksegenskaper, Optional<Behandling> behandling) {
        var egenskaper = new LosFagsakEgenskaperDto(saksegenskaper);
        var oppgaveGrunnlag = OppgaveGrunnlagUtleder.lagGrunnlag(dto, egenskaper);
        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());
        behandlingTjeneste.lagreBehandling(dto, Fagsystem.FPSAK, behandling, kriterier);
    }

    private Optional<Behandling> finnAssertBehandling(UUID behandlingUuid, BehandlingTilstand tilstand, Set<AndreKriterierType> kriterier) {
        var behandling = oppgaveRepository.finnBehandling(behandlingUuid);
        Assertions.assertThat(behandling).isPresent();
        Assertions.assertThat(behandling.get().getBehandlingTilstand()).isEqualTo(tilstand);
        var egenskaper = oppgaveRepository.finnBehandlingKriterier(behandlingUuid);
        Assertions.assertThat(egenskaper).hasSameSizeAs(kriterier);
        Assertions.assertThat(egenskaper).containsExactlyInAnyOrderElementsOf(kriterier);
        return behandling;
    }


    static LosBehandlingDto lagSekvensDto(UUID behandling, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(behandling, Kildesystem.FPSAK, "42", Ytelse.FORELDREPENGER, new AktørId("1234567890123"),
            Behandlingstype.FØRSTEGANGS, Behandlingsstatus.UTREDES, LocalDateTime.now(), AVDELING_BERGEN_ENHET, LocalDate.now(),
            "z999999", List.of(dto), List.of(), false,
            true, List.of(), new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now()), List.of(), null);
    }

    static LosBehandlingDto lagSekvensDtoAvsluttet(UUID behandling, LosAksjonspunktDto... dto) {
        return new LosBehandlingDto(behandling, Kildesystem.FPSAK, "42", Ytelse.FORELDREPENGER, new AktørId("1234567890123"),
            Behandlingstype.FØRSTEGANGS, Behandlingsstatus.AVSLUTTET, LocalDateTime.now(), AVDELING_BERGEN_ENHET, LocalDate.now(),
            "z999999", List.of(dto), List.of(), false,
            true, List.of(), new LosBehandlingDto.LosForeldrepengerDto(LocalDate.now()), List.of(), null);
    }

}
