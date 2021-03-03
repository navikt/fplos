package no.nav.foreldrepenger.los.oppgave.kø;


import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepositoryImpl;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class OppgaveKøTjenesteTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;

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
        this.entityManager = entityManager;
    }

    @Test
    public void testToFiltreringerpåBehandlingstype() {
        Long listeId = leggeInnEtSettMedOppgaver();
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.FØRSTEGANGSSØKNAD, true);
        avdelingslederTjeneste.endreFiltreringBehandlingType(listeId, BehandlingType.KLAGE, true);
        List<Oppgave> oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(2);
    }

    @Test
    public void testFiltreringerpåAndreKriteriertype() {
        Long listeId = leggeInnEtSettMedAndreKriterierOppgaver();
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(listeId, AndreKriterierType.TIL_BESLUTTER, true, true);
        avdelingslederTjeneste.endreFiltreringAndreKriterierType(listeId, AndreKriterierType.PAPIRSØKNAD, true, true);
        List<Oppgave> oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    public void testUtenFiltreringpåBehandlingstype() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        List<Oppgave> oppgaver = oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId);
        assertThat(oppgaver).hasSize(3);
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

        List<OppgaveFiltrering> oppgaveFiltrerings = oppgaveKøTjeneste.hentAlleOppgaveFiltrering(saksbehandler.getSaksbehandlerIdent());
        assertThat(oppgaveFiltrerings).contains(lagtInnLister.get(0), lagtInnLister.get(2));
        assertThat(oppgaveFiltrerings).doesNotContain(lagtInnLister.get(1));
    }

    @Test
    public void hentAntallOppgaver() {
        Long oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        Integer antallOppgaver = oppgaveKøTjeneste.hentAntallOppgaver(oppgaveFiltreringId, false);
        assertThat(antallOppgaver).isEqualTo(3);
    }

    @Test
    public void hentAntallOppgaverForAvdeling() {
        leggeInnEtSettMedOppgaver();
        Integer antallOppgaverDrammen = oppgaveKøTjeneste.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverDrammen).isEqualTo(3);
        Integer antallOppgaverBergen = oppgaveKøTjeneste.hentAntallOppgaverForAvdeling(AVDELING_BERGEN_ENHET);
        assertThat(antallOppgaverBergen).isEqualTo(1);
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
}
