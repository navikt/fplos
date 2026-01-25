package no.nav.foreldrepenger.los.hendelse.behandlinghendelse;

import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Aksjonspunkt;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.AksjonspunktType;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.BehandlingStatus;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Behandlingsegenskap;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Behandlingsårsak;
import static no.nav.foreldrepenger.los.hendelse.behandlinghendelse.OppgaveGrunnlag.Saksegenskap;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;

class KriterieUtlederTest {

    public static final String ENHET_ID = "4401";
    private static final Saksnummer SAKSNUMMER = new Saksnummer("123456");

    @Test
    void skalUtledePapirsøknadKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.PAPIRSØKNAD, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    void skalUtledeTilBeslutterKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.TIL_BESLUTTER, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.TIL_BESLUTTER);
    }

    @Test
    void skalUtledeTerminbekreftelseKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.KONTROLLER_TERMINBEKREFTELSE,
            Aksjonspunktstatus.OPPRETTET, LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.TERMINBEKREFTELSE);
    }

    @Test
    void skalUtledeArbeidOgInntektKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.ARBEID_OG_INNTEKT, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.ARBEID_INNTEKT);
    }

    @Test
    void skalUtledeFormkravKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.VURDER_FORMKRAV, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_FORMKRAV);
    }

    @Test
    void skalUtledeEØSOpptjeningKriterieMedAutomatiskMarkeringOgBosattUtland() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND,
            Aksjonspunktstatus.OPPRETTET, LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunktOgSaksegenskap(aksjonspunkt, Saksegenskap.BOSATT_UTLAND);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void skalUtledeEØSOpptjeningKriterieMedAutomatiskMarkeringOgEØSBosattNorge() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.AUTOMATISK_MARKERING_SOM_UTLAND,
            Aksjonspunktstatus.OPPRETTET, LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunktOgSaksegenskap(aksjonspunkt, Saksegenskap.EØS_BOSATT_NORGE);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void skalUtledeNæringKriterieMedVurderNæringAksjonspunktOgNæring() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.VURDER_NÆRING, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunktOgSaksegenskap(aksjonspunkt, Saksegenskap.NÆRING);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.NÆRING);
    }

    @Test
    void skalUtledePraksisUtsettelseKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.PRAKSIS_UTSETTELSE);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.PRAKSIS_UTSETTELSE);
    }

    @Test
    void skalUtledeEØSSakKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.EØS_BOSATT_NORGE);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.EØS_SAK);
    }

    @Test
    void skalUtledeUtlandssakKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.BOSATT_UTLAND);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    void skalUtledeSammensattKontrollKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.SAMMENSATT_KONTROLL);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.SAMMENSATT_KONTROLL);
    }

    @Test
    void skalUtledeDødKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.DØD);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.DØD);
    }

    @Test
    void skalUtledeBareFantRettKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.BARE_FAR_RETT);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.BARE_FAR_RETT);
    }

    @Test
    void skalUtledeHasterKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.HASTER);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.HASTER);
    }

    @Test
    void skalUtledeMorUkjentUtlandKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsegenskap(Behandlingsegenskap.MOR_UKJENT_UTLAND);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.MOR_UKJENT_UTLAND);
    }

    @Test
    void skalUtledeSykdomsvurderingKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsegenskap(Behandlingsegenskap.SYKDOMSVURDERING);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_SYKDOM);
    }

    @Test
    void skalUtledeOverFireRettsgebyrKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsegenskap(Behandlingsegenskap.TILBAKEKREVING_OVER_FIRE_RETTSGEBYR);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.OVER_FIRE_RETTSGEBYR);
    }

    @Test
    void skalUtledeIkkeVarsletKriterieTilbakebetaling() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedTilbakebetaling(BehandlingType.TILBAKEBETALING,
            List.of(Behandlingsegenskap.SYKDOMSVURDERING), false);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.IKKE_VARSLET);
    }

    @Test
    void skalIkkeUtledeIkkeVarsletNårVarsletEr() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedTilbakebetaling(BehandlingType.TILBAKEBETALING,
            List.of(Behandlingsegenskap.TILBAKEKREVING_SENDT_VARSEL), false);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).doesNotContain(AndreKriterierType.IKKE_VARSLET);
    }

    @Test
    void skalUtledeUtbetalingTilBrukerNårIngenRefusjonskrav() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedRefusjonskrav(false);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.UTBETALING_TIL_BRUKER);
    }

    @Test
    void skalUtledeUtbetalingTilBrukerNårDirecteUtbetaling() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedRefusjonskravOgBehandlingsegenskap(true, Behandlingsegenskap.DIREKTE_UTBETALING);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.UTBETALING_TIL_BRUKER);
    }

    @Test
    void skalUtledeFaresignalKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedFaresignaler(true);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    void skalUtledeFaresignalKriterieNårBehandlingsegenskapInneholder() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsegenskap(Behandlingsegenskap.FARESIGNALER);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.VURDER_FARESIGNALER);
    }

    @Test
    void skalUtledePleiepengerKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak.PLEIEPENGER);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.PLEIEPENGER);
    }

    @Test
    void skalUtledeUtsattStartKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak.UTSATT_START);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.UTSATT_START);
    }

    @Test
    void skalUtledeNyttVedtakKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak.OPPHØR_NY_SAK);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.NYTT_VEDTAK);
    }

    @Test
    void skalUtledeBerørtBehandlingKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak.BERØRT);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.BERØRT_BEHANDLING);
    }

    @Test
    void skalUtledeKlagePåTilbakebetatingKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak.KLAGE_TILBAKEBETALING);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.KLAGE_PÅ_TILBAKEBETALING);
    }

    @Test
    void skalUtledeEndringssøknadKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingstype(FagsakYtelseType.FORELDREPENGER, BehandlingType.REVURDERING,
            List.of(Behandlingsårsak.SØKNAD));

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.ENDRINGSSØKNAD);
    }

    @Test
    void skalUtledeRevurderingInntektsmeldinKriterie() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedBehandlingstype(FagsakYtelseType.FORELDREPENGER, BehandlingType.REVURDERING,
            List.of(Behandlingsårsak.INNTEKTSMELDING));

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.REVURDERING_INNTEKTSMELDING);
    }

    @Test
    void skalUtledeReturFraBeslutterKriterie() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.TIL_BESLUTTER, Aksjonspunktstatus.AVBRUTT,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.RETURNERT_FRA_BESLUTTER);
    }

    @Test
    void skalIkkeUtledePapirsøknadHvisIkkeOpprettet() {
        var aksjonspunkt = new Aksjonspunkt(AksjonspunktType.PAPIRSØKNAD, Aksjonspunktstatus.UTFØRT,
            LocalDateTime.now());
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(aksjonspunkt);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).doesNotContain(AndreKriterierType.PAPIRSØKNAD);
    }

    @Test
    void skalIkkeUtledeEØSOpptjeningUtenAksjonspunkt() {
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap.BOSATT_UTLAND);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).doesNotContain(AndreKriterierType.VURDER_EØS_OPPTJENING);
    }

    @Test
    void skalUtledeFlereKriterier() {
        var aksjonspunkt1 = new Aksjonspunkt(AksjonspunktType.PAPIRSØKNAD, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var aksjonspunkt2 = new Aksjonspunkt(AksjonspunktType.VURDER_FORMKRAV, Aksjonspunktstatus.OPPRETTET,
            LocalDateTime.now());
        var oppgaveGrunnlag = new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(),
            BehandlingType.FØRSTEGANGSSØKNAD, LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), "Z999999",
            List.of(aksjonspunkt1, aksjonspunkt2), List.of(), false, false, List.of(Saksegenskap.DØD), LocalDate.now().plusMonths(1),
            List.of(), BehandlingStatus.OPPRETTET);

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of());

        assertThat(kriterier).contains(AndreKriterierType.PAPIRSØKNAD, AndreKriterierType.DØD);
    }

    @Test
    void skalLeggeTilBeskyttelsesbehovKriterier() {
        var beskyttelsesKriterie = AndreKriterierType.VURDER_FARESIGNALER;
        var oppgaveGrunnlag = lagOppgaveGrunnlagMedAksjonspunkt(
            new Aksjonspunkt(AksjonspunktType.PAPIRSØKNAD, Aksjonspunktstatus.OPPRETTET, LocalDateTime.now()));

        var kriterier = KriterieUtleder.utledKriterier(oppgaveGrunnlag, Set.of(beskyttelsesKriterie));

        assertThat(kriterier).contains(beskyttelsesKriterie, AndreKriterierType.PAPIRSØKNAD);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedAksjonspunkt(Aksjonspunkt aksjonspunkt) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(aksjonspunkt), List.of(), false, false, List.of(),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedAksjonspunktOgSaksegenskap(Aksjonspunkt aksjonspunkt,
                                                                            Saksegenskap saksegenskap) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(aksjonspunkt), List.of(), false, false, List.of(saksegenskap),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedSaksegenskap(Saksegenskap saksegenskap) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), false, false, List.of(saksegenskap),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedBehandlingsegenskap(Behandlingsegenskap behandlingsegenskap) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), false, false, List.of(),
            LocalDate.now().plusMonths(1), List.of(behandlingsegenskap), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedTilbakebetaling(BehandlingType behandlingType,
                                                                 List<Behandlingsegenskap> behandlingsegenskaper,
                                                                 boolean refusjonskrav) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), behandlingType,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), false, refusjonskrav, List.of(),
            LocalDate.now().plusMonths(1), behandlingsegenskaper, BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedRefusjonskrav(boolean refusjonskrav) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), false, refusjonskrav, List.of(),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedRefusjonskravOgBehandlingsegenskap(boolean refusjonskrav,
                                                                                    Behandlingsegenskap behandlingsegenskap) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), false, refusjonskrav, List.of(),
            LocalDate.now().plusMonths(1), List.of(behandlingsegenskap), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedFaresignaler(boolean faresignaler) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(), faresignaler, false, List.of(),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedBehandlingsårsak(Behandlingsårsak behandlingsårsak) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, FagsakYtelseType.FORELDREPENGER, AktørId.dummy(), BehandlingType.FØRSTEGANGSSØKNAD,
            LocalDateTime.now(), ENHET_ID, LocalDate.now().plusDays(10), null, List.of(), List.of(behandlingsårsak), false, false, List.of(),
            LocalDate.now().plusMonths(1), List.of(), BehandlingStatus.OPPRETTET);
    }

    private OppgaveGrunnlag lagOppgaveGrunnlagMedBehandlingstype(FagsakYtelseType ytelse,
                                                                 BehandlingType behandlingType,
                                                                 List<Behandlingsårsak> behandlingsårsaker) {
        return new OppgaveGrunnlag(UUID.randomUUID(), SAKSNUMMER, ytelse, AktørId.dummy(), behandlingType, LocalDateTime.now(), ENHET_ID,
            LocalDate.now().plusDays(10), null, List.of(), behandlingsårsaker, false, false, List.of(), LocalDate.now().plusMonths(1), List.of(),
            BehandlingStatus.OPPRETTET);
    }
}

