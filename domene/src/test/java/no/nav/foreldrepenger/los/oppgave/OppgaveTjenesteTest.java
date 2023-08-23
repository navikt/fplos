package no.nav.foreldrepenger.los.oppgave;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;


@ExtendWith(JpaExtension.class)
class OppgaveTjenesteTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private ReservasjonRepository reservasjonRepository;
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
        oppgaveRepository = new OppgaveRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        reservasjonRepository = new ReservasjonRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, reservasjonRepository);
        this.entityManager = entityManager;
    }


    private Long leggeInnEtSettMedOppgaver() {
        var oppgaveFiltrering = OppgaveFiltrering.builder()
            .medNavn("OPPRETTET")
            .medSortering(KøSortering.OPPRETT_BEHANDLING)
            .medAvdeling(avdelingDrammen())
            .build();
        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
        oppgaveRepository.lagre(førstegangOppgaveBergen);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    private Avdeling avdelingDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class)
            .stream()
            .filter(a -> a.getAvdelingEnhet().equals(AVDELING_DRAMMEN_ENHET))
            .findAny()
            .orElseThrow();
    }

    @Test
    void testEnFiltreringpåBehandlingstype() {
        var listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        var oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    void hentOppgaverSortertPåOpprettet() {
        var andreOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);

        var opprettet = OppgaveFiltrering.builder()
            .medNavn("OPPRETTET")
            .medSortering(KøSortering.OPPRETT_BEHANDLING)
            .medAvdeling(avdelingDrammen())
            .build();
        oppgaveRepository.lagre(opprettet);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(opprettet.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    void hentOppgaverSortertPåFrist() {
        var andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);

        var frist = OppgaveFiltrering.builder().medNavn("FRIST").medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(frist);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(frist.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    void hentOppgaverSortertPåFørsteStønadsdag() {
        var fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 0);
        var førsteOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        var tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 8);
        var andreOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 9);

        var førsteStønadsdag = OppgaveFiltrering.builder()
            .medNavn("STØNADSDAG")
            .medSortering(KøSortering.FØRSTE_STØNADSDAG)
            .medAvdeling(avdelingDrammen())
            .build();
        oppgaveRepository.lagre(førsteStønadsdag);

        var oppgaves = oppgaveKøTjeneste.hentOppgaver(førsteStønadsdag.getId());
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
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).isEmpty();
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).isEmpty();
        assertThat(reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()).isEmpty();

        reservasjonTjeneste.reserverOppgave(førstegangOppgave);
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(2);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_BERGEN_ENHET)).isEmpty();
        var reservasjon = reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()
            .stream()
            .map(Oppgave::getReservasjon)
            .findAny()
            .orElseGet(() -> fail("Ingen oppgave"));
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2);
        assertThat(reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()).hasSize(1);

        reservasjonTjeneste.forlengReservasjonPåOppgave(førstegangOppgave.getId());
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);

        reservasjonTjeneste.endreReservasjonPåOppgave(førstegangOppgave.getId(), LocalDateTime.now().plusDays(3));
        assertThat(reservasjon.getReservertTil().until(LocalDateTime.now().plusDays(3), MINUTES)).isLessThan(2);

        var begrunnelse = "Test";
        reservasjonTjeneste.slettReservasjonMedEventLogg(førstegangOppgave.getReservasjon(), begrunnelse);
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(reservasjonTjeneste.hentSaksbehandlersReserverteAktiveOppgaver()).isEmpty();
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).isEmpty();
        assertThat(reservasjonTjeneste.hentSaksbehandlersSisteReserverteOppgaver()).hasSize(1);
    }

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
