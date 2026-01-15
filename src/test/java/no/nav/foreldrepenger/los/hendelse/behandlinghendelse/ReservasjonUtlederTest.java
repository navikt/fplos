package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static no.nav.foreldrepenger.los.domene.typer.Fagsystem.FPSAK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonKonstanter;

class ReservasjonUtlederTest {

    public static final String ENHET = "4401";
    public static final String SAKSBEHANDLER = "Z999999";
    private ReservasjonUtleder reservasjonUtleder;
    private OppgaveRepository oppgaveRepository;

    @BeforeEach
    void setup() {
        oppgaveRepository = mock(OppgaveRepository.class);
        reservasjonUtleder = new ReservasjonUtleder(oppgaveRepository);
    }

    @Test
    void skalOpprettReservasjonNårIngenEksisterendeOppgaveOgManuellRevurderingMedSaksbehandler() {
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedManuellRevurdering(SAKSBEHANDLER);
        when(oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid())).thenReturn(Optional.empty());

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.empty(), oppgaveGrunnlag);

        assertThat(result).isPresent();
        assertThat(result.get().getReservertAv()).isEqualTo(SAKSBEHANDLER);
        assertThat(result.get().getBegrunnelse()).isNull();
    }

    @Test
    void skalOpprettReservasjonNårIngenEksisterendeOppgaveOgBehandlingPåVentMedSaksbehandler() {
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);
        var behandling = lagBehandling(BehandlingTilstand.VENT_MANUELL);
        when(oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid())).thenReturn(Optional.of(behandling));

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.empty(), oppgaveGrunnlag);

        assertThat(result).isPresent();
        assertThat(result.get().getReservertAv()).isEqualTo(SAKSBEHANDLER);
    }

    @Test
    void skalIkkeOpprettReservasjonNårIngenEksisterendeOppgaveOgIngenSaksbehandler() {
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(null);
        when(oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid())).thenReturn(Optional.empty());

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.empty(), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårIngenEksisterendeOppgaveOgIkkeVentEllerManuellRevurdering() {
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);
        var behandling = lagBehandling(BehandlingTilstand.AKSJONSPUNKT);
        when(oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid())).thenReturn(Optional.of(behandling));

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.empty(), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårEnhetErForskjellig() {
        var eksisterendeOppgave = lagOppgaveMedEnhet();
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlagWithEnhet("4402", SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalOpprettReservasjonMedBegrunnelseNårOppgaveReturnertFraBeslutter() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.TIL_BESLUTTER);
        var nyOppgave = lagOppgaveMedKriterie(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isPresent();
        assertThat(result.get().getReservertAv()).isEqualTo(SAKSBEHANDLER);
        assertThat(result.get().getBegrunnelse()).isEqualTo(ReservasjonKonstanter.RETUR_FRA_BESLUTTER);
    }

    @Test
    void skalIkkeOpprettReservasjonNårBeggeHarPapirsøknadKriterie() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        eksisterendeOppgave.getOppgaveEgenskaper();

        var nyOppgave = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårPapirsøknadBlirFjernet() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårBeggeHarTilBeslutterKriterie() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.TIL_BESLUTTER);
        var nyOppgave = lagOppgaveMedKriterie(AndreKriterierType.TIL_BESLUTTER);
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårEksisterendeHarTilBeslutterMenNyIkke() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.TIL_BESLUTTER);
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårBeggeHarPapirsøknadUtenTilBeslutter() {
        var eksisterendeOppgave = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var nyOppgave = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårEksisterendeOppgaveHarIkkeAktivReservasjon() {
        var eksisterendeOppgave = lagOppgaveMedEnhet();
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.of(eksisterendeOppgave), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @Test
    void skalIkkeOpprettReservasjonNårPapirsøknadBliverBeholdt() {
        var oppgave1 = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var oppgave2 = lagOppgaveMedKriterie(AndreKriterierType.PAPIRSØKNAD);
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);

        var result = reservasjonUtleder.utledReservasjon(oppgave2, Optional.of(oppgave1), oppgaveGrunnlag);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"VENT_KOMPLETT", "VENT_KØ", "VENT_MANUELL", "VENT_REGISTERDATA", "VENT_TIDLIG", "VENT_KLAGEINSTANS", "VENT_SØKNAD"})
    void skalOpprettReservasjonForAlleVentTilstander(String tilstandStr) {
        var nyOppgave = lagOppgaveMedEnhet();
        var oppgaveGrunnlag = lagOppgaveGrunnlag(SAKSBEHANDLER);
        var tilstand = BehandlingTilstand.valueOf(tilstandStr);
        var behandling = lagBehandling(tilstand);
        when(oppgaveRepository.finnBehandling(oppgaveGrunnlag.behandlingUuid())).thenReturn(Optional.of(behandling));

        var result = reservasjonUtleder.utledReservasjon(nyOppgave, Optional.empty(), oppgaveGrunnlag);

        assertThat(result).isPresent().hasValueSatisfying(res -> assertThat(res.getReservertAv()).isEqualTo(SAKSBEHANDLER));
    }

    private static Oppgave lagOppgaveMedEnhet() {
        return Oppgave.builder()
            .medBehandlingId(new no.nav.foreldrepenger.los.domene.typer.BehandlingId(UUID.randomUUID()))
            .medSaksnummer(new Saksnummer("123456"))
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(ReservasjonUtlederTest.ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medAktiv(true)
            .medBehandlingsfrist(LocalDateTime.now().plusDays(10))
            .medBehandlingOpprettet(LocalDateTime.now())
            .medFørsteStønadsdag(LocalDate.now().plusMonths(1))
            .build();
    }

    private Oppgave lagOppgaveMedKriterie(AndreKriterierType kriteria) {
        var oppgave = lagOppgaveMedEnhet();
        var builder = OppgaveEgenskap.builder().medAndreKriterierType(kriteria);
        if (kriteria.erTilBeslutter()) {
            builder.medSisteSaksbehandlerForTotrinn(SAKSBEHANDLER);
        }
        var egenskap = builder.build();
        oppgave.leggTilOppgaveEgenskap(egenskap);
        return oppgave;
    }

    private OppgaveGrunnlag lagOppgaveGrunnlag(String ansvarligSaksbehandler) {
        return lagOppgaveGrunnlagWithEnhet(ENHET, ansvarligSaksbehandler);
    }

    private static OppgaveGrunnlag lagOppgaveGrunnlagWithEnhet(String enhet, String ansvarligSaksbehandler) {
        var uuid = UUID.randomUUID();
        return new OppgaveGrunnlag(uuid, new Saksnummer("123456"), FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), enhet, LocalDate.now().plusDays(10), ansvarligSaksbehandler, List.of(), List.of(), false, false, List.of(),
            LocalDate.now().plusMonths(1), List.of(), OppgaveGrunnlag.BehandlingStatus.OPPRETTET);
    }

    private static OppgaveGrunnlag lagOppgaveGrunnlagMedManuellRevurdering(String ansvarligSaksbehandler) {
        return new OppgaveGrunnlag(UUID.randomUUID(), new Saksnummer("123456"), FagsakYtelseType.FORELDREPENGER, AktørId.dummy(),
            BehandlingType.REVURDERING, LocalDateTime.now(), ENHET, LocalDate.now().plusDays(10), ansvarligSaksbehandler, List.of(),
            List.of(OppgaveGrunnlag.Behandlingsårsak.MANUELL), false, false, List.of(), LocalDate.now().plusMonths(1), List.of(),
            OppgaveGrunnlag.BehandlingStatus.OPPRETTET);
    }

    private static Behandling lagBehandling(BehandlingTilstand tilstand) {
        return Behandling.builder(Optional.empty()).dummyBehandling(ENHET, tilstand).medKildeSystem(FPSAK).build();
    }
}

