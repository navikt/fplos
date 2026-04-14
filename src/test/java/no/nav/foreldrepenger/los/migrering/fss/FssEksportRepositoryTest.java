package no.nav.foreldrepenger.los.migrering.fss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Behandling;
import no.nav.foreldrepenger.los.oppgave.BehandlingEgenskap;
import no.nav.foreldrepenger.los.oppgave.BehandlingTilstand;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.Reservasjon;
import no.nav.foreldrepenger.los.statistikk.StatistikkEnhetYtelseBehandling;
import no.nav.foreldrepenger.los.statistikk.kø.InnslagType;
import no.nav.foreldrepenger.los.statistikk.kø.StatistikkOppgaveFilter;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class FssEksportRepositoryTest {

    private static final String ENHET = Avdeling.AVDELING_DRAMMEN_ENHET;

    private EntityManager entityManager;
    private FssEksportRepository repository;
    private OrganisasjonRepository organisasjonRepository;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.organisasjonRepository = mock(OrganisasjonRepository.class);
        this.repository = new FssEksportRepository(entityManager, organisasjonRepository);
    }

    @Test
    void hentBehandlinger_filtrererBortAvsluttede() {
        var aktiv = lagBehandling(BehandlingTilstand.OPPRETTET);
        var avsluttet = lagBehandling(BehandlingTilstand.AVSLUTTET);
        entityManager.persist(aktiv);
        entityManager.persist(avsluttet);
        entityManager.flush();

        var result = repository.hentBehandlinger(0, 100);

        assertThat(result.behandlinger()).hasSize(1);
        assertThat(result.behandlinger().getFirst().id()).isEqualTo(aktiv.getId());
    }

    @Test
    void hentBehandlinger_medEgenskaper() {
        var behandling = lagBehandling(BehandlingTilstand.OPPRETTET);
        entityManager.persist(behandling);
        entityManager.persist(new BehandlingEgenskap(behandling.getId(), AndreKriterierType.PAPIRSØKNAD));
        entityManager.persist(new BehandlingEgenskap(behandling.getId(), AndreKriterierType.UTBETALING_TIL_BRUKER));
        entityManager.flush();

        var result = repository.hentBehandlinger(0, 100);

        assertThat(result.behandlinger()).hasSize(1);
        assertThat(result.behandlinger().getFirst().egenskaper()).containsExactlyInAnyOrder(AndreKriterierType.PAPIRSØKNAD,
            AndreKriterierType.UTBETALING_TIL_BRUKER);
    }

    @Test
    void hentBehandlinger_utenEgenskaper_girTomtSett() {
        var behandling = lagBehandling(BehandlingTilstand.OPPRETTET);
        entityManager.persist(behandling);
        entityManager.flush();

        var result = repository.hentBehandlinger(0, 100);

        assertThat(result.behandlinger()).hasSize(1);
        assertThat(result.behandlinger().getFirst().egenskaper()).isEmpty();
    }

    @Test
    void hentBehandlinger_tomtResultat() {
        var result = repository.hentBehandlinger(0, 100);
        assertThat(result.behandlinger()).isEmpty();
    }

    @Test
    void hentBehandlinger_paginering() {
        for (int i = 0; i < 5; i++) {
            entityManager.persist(lagBehandling(BehandlingTilstand.OPPRETTET));
        }
        entityManager.flush();

        var side1 = repository.hentBehandlinger(0, 2);
        var side2 = repository.hentBehandlinger(2, 2);
        var side3 = repository.hentBehandlinger(4, 2);

        assertThat(side1.behandlinger()).hasSize(2);
        assertThat(side2.behandlinger()).hasSize(2);
        assertThat(side3.behandlinger()).hasSize(1);
    }

    @Test
    void hentAktiveOppgaver_returnererBareAktive() {
        var oppgaveAktiv = lagOppgave(true);
        var oppgaveInaktiv = lagOppgave(false);
        entityManager.persist(oppgaveAktiv);
        entityManager.persist(oppgaveInaktiv);
        entityManager.flush();

        var result = repository.hentAktiveOppgaverOgReservasjoner(0, 100);

        assertThat(result.aktiveOppgaver()).hasSize(1);
        assertThat(result.aktiveOppgaver().getFirst().aktiv()).isTrue();
    }

    @Test
    void hentInaktiveOppgaver_medReservasjonOgIkkeAvsluttetBehandling() {
        var behandling = lagBehandling(BehandlingTilstand.OPPRETTET);
        entityManager.persist(behandling);

        var oppgave = Oppgave.builder()
            .medBehandlingId(new BehandlingId(behandling.getId()))
            .medSaksnummer(new Saksnummer(String.valueOf(System.nanoTime())))
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(ENHET)
            .medAktiv(false)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medSystem(Fagsystem.FPSAK)
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlingOpprettet(LocalDateTime.now())
            .build();
        entityManager.persist(oppgave);

        var reservasjon = new Reservasjon(oppgave);
        reservasjon.setReservertAv("Z999999");
        reservasjon.setReservertTil(LocalDateTime.now().plusDays(1));
        entityManager.persist(reservasjon);
        entityManager.flush();

        var result = repository.hentInaktiveOppgaverOgReservasjoner(0, 100);

        assertThat(result.inaktiveOppgaver()).hasSize(1);
    }

    @Test
    void hentInaktiveOppgaver_ekskludererAvsluttetBehandling() {
        var behandling = lagBehandling(BehandlingTilstand.AVSLUTTET);
        entityManager.persist(behandling);

        var oppgave = Oppgave.builder()
            .medBehandlingId(new BehandlingId(behandling.getId()))
            .medSaksnummer(new Saksnummer(String.valueOf(System.nanoTime())))
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(ENHET)
            .medAktiv(false)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medSystem(Fagsystem.FPSAK)
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlingOpprettet(LocalDateTime.now())
            .build();
        entityManager.persist(oppgave);

        var reservasjon = new Reservasjon(oppgave);
        reservasjon.setReservertAv("Z999999");
        reservasjon.setReservertTil(LocalDateTime.now().plusDays(1));
        entityManager.persist(reservasjon);
        entityManager.flush();

        var result = repository.hentInaktiveOppgaverOgReservasjoner(0, 100);

        assertThat(result.inaktiveOppgaver()).isEmpty();
    }

    @Test
    void hentStatistikkEnhetYtelseBehandling_henterAlleOgSortererPåTidsstempel() {
        var dato = LocalDate.now();
        entityManager.persist(
            new StatistikkEnhetYtelseBehandling(ENHET, 200L, FagsakYtelseType.FORELDREPENGER, BehandlingType.FØRSTEGANGSSØKNAD, dato, 5, 2, 1));
        entityManager.persist(
            new StatistikkEnhetYtelseBehandling(ENHET, 100L, FagsakYtelseType.FORELDREPENGER, BehandlingType.FØRSTEGANGSSØKNAD, dato, 3, 1, 0));
        entityManager.flush();

        var result = repository.hentStatistikkEnhetYtelseBehandling(0, 100);

        assertThat(result.statistikkEnhetYtelseBehandling()).hasSize(2);
        assertThat(result.statistikkEnhetYtelseBehandling().getFirst().tidsstempel()).isEqualTo(100L);
        assertThat(result.statistikkEnhetYtelseBehandling().getLast().tidsstempel()).isEqualTo(200L);
    }

    @Test
    void hentStatistikkOppgaveFilter_filtrererPåDato() {
        var nylig = LocalDate.now().minusDays(7);
        var gammel = LocalDate.now().minusWeeks(6);
        var nå = toMs(LocalDateTime.now());
        entityManager.persist(new StatistikkOppgaveFilter(1L, nå, nylig, 5, 3, 1, 2, 4, InnslagType.REGELMESSIG));
        entityManager.persist(new StatistikkOppgaveFilter(2L, nå - 1, gammel, 3, 1, 0, 1, 2, InnslagType.REGELMESSIG));
        entityManager.flush();

        var result = repository.hentStatistikkOppgaveFilter(0, 100);

        assertThat(result.statistikkOppgaveFilter()).hasSize(1);
        assertThat(result.statistikkOppgaveFilter().getFirst().oppgaveFilterId()).isEqualTo(1L);
    }

    @Test
    void hentOrganisasjonOgKøer_returData() {
        var avdeling = new Avdeling(ENHET, "NAV Drammen", false);
        entityManager.persist(avdeling);
        entityManager.flush();

        when(organisasjonRepository.hentAktiveAvdelinger()).thenReturn(List.of(avdeling));

        var result = repository.hentOrganisasjonOgKøer();

        assertThat(result.organisasjonData()).isNotNull();
        assertThat(result.organisasjonData().avdelinger()).hasSize(1);
        assertThat(result.organisasjonData().avdelinger().getFirst().avdelingEnhet()).isEqualTo(ENHET);
    }

    private Behandling lagBehandling(BehandlingTilstand tilstand) {
        return Behandling.builder(Optional.empty())
            .medId(UUID.randomUUID())
            .medSaksnummer(new Saksnummer(String.valueOf(System.nanoTime())))
            .medAktørId(AktørId.dummy())
            .medKildeSystem(Fagsystem.FPSAK)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medBehandlingTilstand(tilstand)
            .medBehandlendeEnhet(ENHET)
            .medOpprettet(LocalDateTime.now())
            .medBehandlingsfrist(LocalDate.now().plusDays(7))
            .build();
    }

    private Oppgave lagOppgave(boolean aktiv) {
        return Oppgave.builder()
            .medBehandlingId(BehandlingId.random())
            .medSaksnummer(new Saksnummer(String.valueOf(System.nanoTime())))
            .medAktørId(AktørId.dummy())
            .medBehandlendeEnhet(ENHET)
            .medAktiv(aktiv)
            .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medSystem(Fagsystem.FPSAK)
            .medBehandlingsfrist(LocalDateTime.now())
            .medBehandlingOpprettet(LocalDateTime.now())
            .build();
    }

    private static Long toMs(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
