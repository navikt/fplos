package no.nav.foreldrepenger.los.oppgave;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepositoryImpl;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjenesteImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;


@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveTjenesteImplTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;
    private OppgaveKøTjeneste oppgaveKøTjeneste;
    private OppgaveTjeneste oppgaveTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;

    private AvdelingslederTjeneste avdelingslederTjeneste;

    private final Oppgave førstegangOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private final Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.KLAGE).build();
    private final Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.INNSYN).build();
    private final Oppgave førstegangOppgaveBergen = Oppgave.builder().dummyOppgave(AVDELING_BERGEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private EntityManager entityManager;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository);
        reservasjonTjeneste = new ReservasjonTjenesteImpl(oppgaveRepository);
        this.entityManager = entityManager;
    }


    private Long leggeInnEtSettMedOppgaver() {
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET")
                .medSortering(KøSortering.OPPRETT_BEHANDLING)
                .medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
        oppgaveRepository.lagre(førstegangOppgaveBergen);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    private Avdeling avdelingDrammen() {
        return DBTestUtil.hentAlle(entityManager, Avdeling.class).stream()
                .filter(a -> a.getAvdelingEnhet().equals(AVDELING_DRAMMEN_ENHET))
                .findAny().orElseThrow();
    }

    @Test
    public void testEnFiltreringpåBehandlingstype() {
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        List<Oppgave> oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId);
        Assertions.assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void hentOppgaverSortertPåOpprettet() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);

        OppgaveFiltrering opprettet = OppgaveFiltrering.builder().medNavn("OPPRETTET")
                .medSortering(KøSortering.OPPRETT_BEHANDLING)
                .medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(opprettet);

        List<Oppgave> oppgaves = oppgaveKøTjeneste.hentOppgaver(opprettet.getId());
        Assertions.assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    public void hentOppgaverSortertPåFrist() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 8);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 0);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 10);
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 9);

        OppgaveFiltrering frist = OppgaveFiltrering.builder().medNavn("FRIST")
                .medSortering(KøSortering.BEHANDLINGSFRIST)
                .medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(frist);

        List<Oppgave> oppgaves = oppgaveKøTjeneste.hentOppgaver(frist.getId());
        Assertions.assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }

    @Test
    public void hentOppgaverSortertPåFørsteStonadsdag() {
        Oppgave fjerdeOppgave = opprettOgLargeOppgaveTilSortering(10, 0, 0);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        Oppgave tredjeOppgave = opprettOgLargeOppgaveTilSortering(9, 8, 8);
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 9);

        OppgaveFiltrering førsteStønadsdag = OppgaveFiltrering.builder().medNavn("STØNADSDAG")
                .medSortering(KøSortering.FORSTE_STONADSDAG)
                .medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(førsteStønadsdag);

        List<Oppgave> oppgaves = oppgaveKøTjeneste.hentOppgaver(førsteStønadsdag.getId());
        Assertions.assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
    }


    private Oppgave opprettOgLargeOppgaveTilSortering(int dagerSidenOpprettet, int dagersidenBehandlingsFristGikkUt, int førsteStønadsdag) {
        Oppgave oppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET)
                .medBehandlingOpprettet(LocalDateTime.now().minusDays(dagerSidenOpprettet))
                .medBehandlingsfrist(LocalDateTime.now().minusDays(dagersidenBehandlingsFristGikkUt))
                .medForsteStonadsdag(LocalDate.now().minusDays(førsteStønadsdag))
                .build();
        oppgaveRepository.lagre(oppgave);
        return oppgave;
    }

    @Test
    public void testReservasjon() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(0);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(0);
        assertThat(reservasjonTjeneste.hentSisteReserverteOppgaver()).hasSize(0);

        reservasjonTjeneste.reserverOppgave(førstegangOppgave.getId());
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(2);
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(1);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_BERGEN_ENHET)).hasSize(0);
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2);
        assertThat(reservasjonTjeneste.hentSisteReserverteOppgaver()).hasSize(1);

        reservasjonTjeneste.forlengReservasjonPåOppgave(førstegangOppgave.getId());
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET).get(0).getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);

        reservasjonTjeneste.endreReservasjonPåOppgave(førstegangOppgave.getId(), LocalDateTime.now().plusDays(3));
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusDays(3), MINUTES)).isLessThan(2);

        String begrunnelse = "Test";
        reservasjonTjeneste.frigiOppgave(førstegangOppgave.getId(), begrunnelse);
        assertThat(oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(reservasjonTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(0);
        assertThat(reservasjonTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(0);
        assertThat(reservasjonTjeneste.hentSisteReserverteOppgaver()).hasSize(1);
        assertThat(reservasjonTjeneste.hentSisteReserverteOppgaver().get(0).getReservasjon().getBegrunnelse()).isEqualTo(begrunnelse);
    }

    @Test
    public void testOppgaverForandret() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 10);
        List<Long> oppgaveIder = Arrays.asList(førsteOppgave.getId(), andreOppgave.getId());
        assertThat(oppgaveTjeneste.erAlleOppgaverFortsattTilgjengelig(oppgaveIder)).isTrue();
        reservasjonTjeneste.reserverOppgave(førsteOppgave.getId());
        assertThat(oppgaveTjeneste.erAlleOppgaverFortsattTilgjengelig(oppgaveIder)).isFalse();
    }
}
