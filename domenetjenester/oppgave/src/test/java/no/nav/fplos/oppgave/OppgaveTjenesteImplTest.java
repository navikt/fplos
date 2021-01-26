package no.nav.fplos.oppgave;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveTjenesteImplTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveTjenesteImpl oppgaveTjeneste;

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
        avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);
        oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository);
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

    private Long leggeInnEtSettMedAndreKriterierOppgaver() {
        OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("OPPRETTET")
                .medSortering(KøSortering.OPPRETT_BEHANDLING)
                .medAvdeling(avdelingDrammen()).build();
        oppgaveRepository.lagre(oppgaveFiltrering);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.TIL_BESLUTTER);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.PAPIRSØKNAD);
        leggtilOppgaveMedEkstraEgenskaper(klageOppgave, AndreKriterierType.PAPIRSØKNAD);
        oppgaveRepository.lagre(innsynOppgave);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

    private void leggtilOppgaveMedEkstraEgenskaper(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.refresh(oppgave);
        oppgaveRepository.lagre(new OppgaveEgenskap(oppgave, andreKriterierType));
    }

    private List<OppgaveFiltrering> leggInnEtSettMedLister(int antallLister) {
        List<OppgaveFiltrering> filtre = new ArrayList<>();
        for (int i = 0; i < antallLister; i++) {
            OppgaveFiltrering oppgaveFiltrering = OppgaveFiltrering.builder()
                    .medNavn("Test " + i).medSortering(KøSortering.BEHANDLINGSFRIST)
                    .medAvdeling(avdelingDrammen()).build();
            entityManager.persist(oppgaveFiltrering);
            filtre.add(oppgaveFiltrering);
        }
        entityManager.flush();
        return filtre;
    }

    @Test
    public void testEnFiltreringpåBehandlingstype() {
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void testToFiltreringerpåBehandlingstype() {
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.KLAGE, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(2);
    }

    @Test
    public void testFiltreringerpåAndreKriteriertype() {
        Long listeId = leggeInnEtSettMedAndreKriterierOppgaver();
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(listeId, AndreKriterierType.TIL_BESLUTTER, true, true);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(listeId, AndreKriterierType.PAPIRSØKNAD, true, true);
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void testUtenFiltreringpåBehandlingstype() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        List<Oppgave> oppgaver = oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId);
        assertThat(oppgaver).hasSize(3);
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

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(opprettet.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
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

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(frist.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
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

        List<Oppgave> oppgaves = oppgaveTjeneste.hentOppgaver(førsteStønadsdag.getId());
        assertThat(oppgaves).containsSequence(førsteOppgave, andreOppgave, tredjeOppgave, fjerdeOppgave);
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
    public void hentAlleOppgaveFiltrering() {
        List<OppgaveFiltrering> lagtInnLister = leggInnEtSettMedLister(3);
        Saksbehandler saksbehandler = new Saksbehandler("1234567");
        entityManager.persist(saksbehandler);
        entityManager.flush();

        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(0).getId(), saksbehandler.getSaksbehandlerIdent());
        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(2).getId(), saksbehandler.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler);

        List<OppgaveFiltrering> oppgaveFiltrerings = oppgaveTjeneste.hentAlleOppgaveFiltrering(saksbehandler.getSaksbehandlerIdent());
        assertThat(oppgaveFiltrerings).contains(lagtInnLister.get(0), lagtInnLister.get(2));
        assertThat(oppgaveFiltrerings).doesNotContain(lagtInnLister.get(1));
    }

    @Test
    public void hentAntallOppgaver() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        Integer antallOppgaver = oppgaveTjeneste.hentAntallOppgaver(oppgaveFiltreringId, false);
        assertThat(antallOppgaver).isEqualTo(3);
    }

    @Test
    public void hentAntallOppgaverForAvdeling() {
        leggeInnEtSettMedOppgaver();
        Integer antallOppgaverDrammen = oppgaveTjeneste.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverDrammen).isEqualTo(3);
        Integer antallOppgaverBergen = oppgaveTjeneste.hentAntallOppgaverForAvdeling(AVDELING_BERGEN_ENHET);
        assertThat(antallOppgaverBergen).isEqualTo(1);
    }

    @Test
    public void testReservasjon() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(0);
        assertThat(oppgaveTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(0);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(0);

        oppgaveTjeneste.reserverOppgave(førstegangOppgave.getId());
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(2);
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(1);
        assertThat(oppgaveTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(1);
        assertThat(oppgaveTjeneste.hentReservasjonerForAvdeling(AVDELING_BERGEN_ENHET)).hasSize(0);
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(1);

        oppgaveTjeneste.forlengReservasjonPåOppgave(førstegangOppgave.getId());
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);
        assertThat(oppgaveTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET).get(0).getReservertTil().until(LocalDateTime.now().plusHours(26), MINUTES)).isLessThan(2);

        oppgaveTjeneste.endreReservasjonPåOppgave(førstegangOppgave.getId(), LocalDateTime.now().plusDays(3));
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver().get(0).getReservertTil().until(LocalDateTime.now().plusDays(3), MINUTES)).isLessThan(2);

        String begrunnelse = "Test";
        oppgaveTjeneste.frigiOppgave(førstegangOppgave.getId(), begrunnelse);
        assertThat(oppgaveTjeneste.hentOppgaver(oppgaveFiltreringId)).hasSize(3);
        assertThat(oppgaveTjeneste.hentReservasjonerTilknyttetAktiveOppgaver()).hasSize(0);
        assertThat(oppgaveTjeneste.hentReservasjonerForAvdeling(AVDELING_DRAMMEN_ENHET)).hasSize(0);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver()).hasSize(1);
        assertThat(oppgaveTjeneste.hentSisteReserverteOppgaver().get(0).getReservasjon().getBegrunnelse()).isEqualTo(begrunnelse);
    }

    @Test
    public void testOppgaverForandret() {
        Oppgave andreOppgave = opprettOgLargeOppgaveTilSortering(8, 9, 10);
        Oppgave førsteOppgave = opprettOgLargeOppgaveTilSortering(0, 10, 10);
        List<Long> oppgaveIder = Arrays.asList(førsteOppgave.getId(), andreOppgave.getId());
        assertThat(oppgaveTjeneste.harForandretOppgaver(oppgaveIder)).isFalse();
        oppgaveTjeneste.reserverOppgave(førsteOppgave.getId());
        assertThat(oppgaveTjeneste.harForandretOppgaver(oppgaveIder)).isTrue();
    }
}
