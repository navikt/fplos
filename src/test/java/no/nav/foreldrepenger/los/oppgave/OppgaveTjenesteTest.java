package no.nav.foreldrepenger.los.oppgave;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.BehandlingTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;


@ExtendWith(JpaExtension.class)
class OppgaveTjenesteTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;
    private AvdelingslederTjeneste avdelingslederTjeneste;

    private final Oppgave førstegangOppgave = Oppgave.builder()
        .dummyOppgave(AVDELING_DRAMMEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();
    private final Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.KLAGE).build();
    private final Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingType(BehandlingType.INNSYN).build();
    private final Oppgave førstegangOppgaveBergen = Oppgave.builder()
        .dummyOppgave(AVDELING_BERGEN_ENHET)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .build();

    @BeforeEach
    void setup(EntityManager entityManager) {
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        oppgaveRepository = new OppgaveRepository(entityManager);
        var oppgaveKøRepository = new OppgaveKøRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, oppgaveKøRepository, organisasjonRepository);
        var reservasjonRepository = new ReservasjonRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, reservasjonRepository, new BehandlingTjeneste(oppgaveRepository));
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        this.entityManager = entityManager;
    }


    private Long leggeInnEtSettMedOppgaver() {
        var oppgaveFiltrering = new OppgaveFiltrering();
        oppgaveFiltrering.setNavn("OPPRETTET");
        oppgaveFiltrering.setSortering(KøSortering.OPPRETT_BEHANDLING);
        oppgaveFiltrering.setAvdeling(avdelingDrammen(entityManager));

        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
        oppgaveRepository.lagre(førstegangOppgaveBergen);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    @Test
    void testEnFiltreringpåBehandlingstype() {
        // Arrange
        var listeId = leggeInnEtSettMedOppgaver();
        var liste = oppgaveRepository.hentOppgaveFilterSett(listeId).orElseThrow();
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(BehandlingType.FØRSTEGANGSSØKNAD),
            Set.of(),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(), Set.of())
        );

        // Act
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);

        // Assert
        var oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId, 100);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    void hentOppgaverSortertPåOpprettet() {
        var andreOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);

        var opprettet = new OppgaveFiltrering();
        opprettet.setNavn("OPPRETTET");
        opprettet.setSortering(KøSortering.OPPRETT_BEHANDLING);
        opprettet.setAvdeling(avdelingDrammen(entityManager));
        oppgaveRepository.lagre(opprettet);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(opprettet.getId(), 100);
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    void hentOppgaverSortertPåFrist() {
        var andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);

        var frist = new OppgaveFiltrering();
        frist.setNavn("FRIST");
        frist.setSortering(KøSortering.BEHANDLINGSFRIST);
        frist.setAvdeling(avdelingDrammen(entityManager));

        oppgaveRepository.lagre(frist);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(frist.getId(), 100);
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    void hentOppgaverSortertPåFørsteStønadsdag() {
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 0);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 8);
        var andreOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 9);

        var førsteStønadsdag = new OppgaveFiltrering();
        førsteStønadsdag.setNavn("STØNADSDAG");
        førsteStønadsdag.setSortering(KøSortering.FØRSTE_STØNADSDAG);
        førsteStønadsdag.setAvdeling(avdelingDrammen(entityManager));
        oppgaveRepository.lagre(førsteStønadsdag);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(førsteStønadsdag.getId(), 100);
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }


    private Oppgave opprettOgLargeOppgaveTilSortering(int dagerSidenOpprettet, int dagersidenBehandlingsFristGikkUt, int førsteStønadsdag) {
        var oppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingOpprettet(LocalDateTime.now().minusDays(dagerSidenOpprettet))
            .medBehandlingsfrist(LocalDateTime.now().minusDays(dagersidenBehandlingsFristGikkUt))
            .medFørsteStønadsdag(LocalDate.now().minusDays(førsteStønadsdag))
            .build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    @Test
    void testReservasjon() {
        var oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId, 100)).hasSize(3);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).isEmpty();
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).isEmpty();

        reservasjonTjeneste.reserverOppgave(førstegangOppgave);
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId, 100)).hasSize(2);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_BERGEN_ENHET)).isEmpty();
        var reservasjon = reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()
            .stream()
            .map(Oppgave::getReservasjon)
            .findAny()
            .orElseGet(() -> fail("Ingen oppgave"));
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2);

        reservasjonTjeneste.forlengReservasjonPåOppgave(førstegangOppgave.getId());
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);

        reservasjonTjeneste.endreReservasjonPåOppgave(førstegangOppgave.getId(), LocalDateTime.now().plusDays(3));
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusDays(3), MINUTES)).isLessThan(2);

        reservasjonTjeneste.slettReservasjon(førstegangOppgave.getReservasjon());
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId, 100)).hasSize(3);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).isEmpty();
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).isEmpty();
    }

    /*
    TODO: oppdater test før merging
    @Test
    void testSisteReserverte() {
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(opprettOppgaveEventLogg(førstegangOppgave));

        reservasjonTjeneste.reserverOppgave(førstegangOppgave);
        var sisteReserverteEtterReservasjon = reservasjonTjeneste.hentSaksbehandlersSisteReserverteMedStatus(false);
        assertThat(sisteReserverteEtterReservasjon)
            .hasSize(1)
            .first().matches(sr -> sr.status() == OppgaveBehandlingStatus.UNDER_ARBEID);

        oppgaveTjeneste.avsluttOppgaveMedEventLogg(førstegangOppgave, OppgaveEventType.LUKKET);
        var sisteReserverte = reservasjonTjeneste.hentSaksbehandlersSisteReserverteMedStatus(false);
        assertThat(sisteReserverte)
            .hasSize(1)
            .first().matches(sr -> sr.status() == OppgaveBehandlingStatus.FERDIG);

        var sisteReserverteAktive = reservasjonTjeneste.hentSaksbehandlersSisteReserverteMedStatus(true);
        assertThat(sisteReserverteAktive).isEmpty();
    }

     */

    @Test
    void testOppgaverForandret() {
        var andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 10);
        var oppgaveIder = Arrays.asList(førsteOppgave.getId(), andreOppgave.getId());
        assertThat(oppgaveTjeneste.erAlleOppgaverFortsattTilgjengelig(oppgaveIder)).isTrue();
        reservasjonTjeneste.reserverOppgave(førsteOppgave);
        assertThat(oppgaveTjeneste.erAlleOppgaverFortsattTilgjengelig(oppgaveIder)).isFalse();
    }
}
