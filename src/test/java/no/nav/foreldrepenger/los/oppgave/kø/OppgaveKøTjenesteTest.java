package no.nav.foreldrepenger.los.oppgave.kø;


import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.los.oppgave.Periodefilter;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Filtreringstype;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;

@ExtendWith(JpaExtension.class)
class OppgaveKøTjenesteTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;

    private AvdelingslederTjeneste avdelingslederTjeneste;
    private OppgaveKøTjeneste oppgaveKøTjeneste;

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
    private EntityManager entityManager;

    @BeforeEach
    void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepository(entityManager);
        var oppgaveKøRepository = new OppgaveKøRepository(entityManager);
        var organisasjonRepository = new OrganisasjonRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        oppgaveKøTjeneste = new OppgaveKøTjeneste(oppgaveRepository, oppgaveKøRepository, organisasjonRepository);
        this.entityManager = entityManager;
    }

    @Test
    void testToFiltreringerpåBehandlingstype() {
        var listeId = leggeInnEtSettMedOppgaver();
        var liste = oppgaveRepository.hentOppgaveFilterSett(listeId).orElseThrow();
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(BehandlingType.FØRSTEGANGSSØKNAD, BehandlingType.KLAGE),
            Set.of(),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(), Set.of())
        );

        // Act
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);

        // Assert
        var oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId, 100);
        assertThat(oppgaver).hasSize(2);
    }

    @Test
    void testFiltreringerpåAndreKriteriertype() {
        var listeId = leggeInnEtSettMedAndreKriterierOppgaver();
        var liste = oppgaveRepository.hentOppgaveFilterSett(listeId).orElseThrow();
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(),
            Set.of(),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(AndreKriterierType.TIL_BESLUTTER, AndreKriterierType.PAPIRSØKNAD), Set.of())
        );

        // Act
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);

        // Assert
        var oppgaver = oppgaveKøTjeneste.hentOppgaver(listeId, 100);
        assertThat(oppgaver).hasSize(1);
    }

    @Test
    void testUtenFiltreringpåBehandlingstype() {
        var oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        var oppgaver = oppgaveKøTjeneste.hentOppgaver(oppgaveFiltreringId, 100);
        assertThat(oppgaver).hasSize(3);
    }

    @Test
    void hentAlleOppgaveFiltrering() {
        var lagtInnLister = leggInnEtSettMedLister(3);
        var saksbehandler = new Saksbehandler("1234567", UUID.randomUUID(), "Navn Navnesen", "1234");
        entityManager.persist(saksbehandler);
        entityManager.flush();

        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(0).getId(), saksbehandler.getSaksbehandlerIdent());
        avdelingslederTjeneste.leggSaksbehandlerTilListe(lagtInnLister.get(2).getId(), saksbehandler.getSaksbehandlerIdent());
        entityManager.refresh(saksbehandler);

        var oppgaveFiltrerings = oppgaveKøTjeneste.hentAlleOppgaveFiltrering(saksbehandler.getSaksbehandlerIdent());
        assertThat(oppgaveFiltrerings).contains(lagtInnLister.get(0), lagtInnLister.get(2)).doesNotContain(lagtInnLister.get(1));
    }

    @Test
    void hentAntallOppgaver() {
        var oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        var antallOppgaver = oppgaveKøTjeneste.hentAntallOppgaver(oppgaveFiltreringId, Filtreringstype.ALLE);
        assertThat(antallOppgaver).isEqualTo(3);
    }

    @Test
    void hentAntallOppgaverForAvdeling() {
        leggeInnEtSettMedOppgaver();
        var antallOppgaverDrammen = oppgaveKøTjeneste.hentAntallOppgaverForAvdeling(AVDELING_DRAMMEN_ENHET);
        assertThat(antallOppgaverDrammen).isEqualTo(3);
        var antallOppgaverBergen = oppgaveKøTjeneste.hentAntallOppgaverForAvdeling(AVDELING_BERGEN_ENHET);
        assertThat(antallOppgaverBergen).isEqualTo(1);
    }


    private Long leggeInnEtSettMedAndreKriterierOppgaver() {
        var oppgaveFiltrering = new OppgaveFiltrering();
        oppgaveFiltrering.setNavn("OPPRETTET");
        oppgaveFiltrering.setSortering(KøSortering.OPPRETT_BEHANDLING);
        oppgaveFiltrering.setAvdeling(avdelingDrammen(entityManager));

        oppgaveRepository.lagre(oppgaveFiltrering);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.TIL_BESLUTTER);
        leggtilOppgaveMedEkstraEgenskaper(førstegangOppgave, AndreKriterierType.PAPIRSØKNAD);
        leggtilOppgaveMedEkstraEgenskaper(klageOppgave, AndreKriterierType.PAPIRSØKNAD);
        oppgaveRepository.lagre(innsynOppgave);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }


    private void leggtilOppgaveMedEkstraEgenskaper(Oppgave oppgave, AndreKriterierType andreKriterierType) {
        var oppgaveEgenskapBuilder = OppgaveEgenskap.builder().medAndreKriterierType(andreKriterierType);
        if (andreKriterierType.erTilBeslutter()) {
            oppgaveEgenskapBuilder.medSisteSaksbehandlerForTotrinn("IDENT");
        }
        oppgave.leggTilOppgaveEgenskap(oppgaveEgenskapBuilder.build());
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.refresh(oppgave);
    }

    private List<OppgaveFiltrering> leggInnEtSettMedLister(int antallLister) {
        List<OppgaveFiltrering> filtre = new ArrayList<>();
        for (var i = 0; i < antallLister; i++) {
            var oppgaveFiltrering = new OppgaveFiltrering();
            oppgaveFiltrering.setNavn("Test " + i);
            oppgaveFiltrering.setSortering(KøSortering.BEHANDLINGSFRIST);
            oppgaveFiltrering.setAvdeling(avdelingDrammen(entityManager));

            entityManager.persist(oppgaveFiltrering);
            filtre.add(oppgaveFiltrering);
        }
        entityManager.flush();
        return filtre;
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


}
