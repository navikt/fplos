package no.nav.foreldrepenger.los.oppgave;


import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.saksliste.dto.SakslisteLagreDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.vedtak.exception.FunksjonellException;

@ExtendWith(JpaExtension.class)
class BehandlingKøRepositoryTest {

    private static final String AVDELING_BERGEN_ENHET = "4812";

    private OppgaveRepository oppgaveRepository;
    private BehandlingKøRepository behandlingKøRepository;

    private AvdelingslederTjeneste avdelingslederTjeneste;

    private final Behandling førstegangBehandlingAksjonspunkt = Behandling.builder(Optional.empty())
        .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.AKSJONSPUNKT)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .medKildeSystem(Fagsystem.FPSAK)
        .medId(UUID.randomUUID())
        .build();
    private final Behandling førstegangBehandlingVent = Behandling.builder(Optional.empty())
        .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.VENT_MANUELL)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .medKildeSystem(Fagsystem.FPSAK)
        .medId(UUID.randomUUID())
        .build();
    private final Behandling klageOppgave = Behandling.builder(Optional.empty())
        .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.VENT_MANUELL)
        .medBehandlingType(BehandlingType.KLAGE)
        .medKildeSystem(Fagsystem.FPSAK)
        .medId(UUID.randomUUID())
        .build();
    private final Behandling innsynOppgave = Behandling.builder(Optional.empty())
        .dummyBehandling(AVDELING_DRAMMEN_ENHET, BehandlingTilstand.VENT_REGISTERDATA)
        .medBehandlingType(BehandlingType.INNSYN)
        .medKildeSystem(Fagsystem.FPSAK)
        .medId(UUID.randomUUID())
        .build();
    private final Behandling førstegangOppgaveBergen = Behandling.builder(Optional.empty())
        .dummyBehandling(AVDELING_BERGEN_ENHET, BehandlingTilstand.VENT_KOMPLETT)
        .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
        .medKildeSystem(Fagsystem.FPSAK)
        .medId(UUID.randomUUID())
        .build();
    private EntityManager entityManager;

    @BeforeEach
    void setup(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepository(entityManager);
        behandlingKøRepository = new BehandlingKøRepository(entityManager);
        avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, new OrganisasjonRepository(entityManager));
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
        var oppgaver = hentAntallOppgaver(listeId);
        assertThat(oppgaver).isEqualTo(2);
    }

    @Test
    void testFiltreringerpåAndreKriteriertype() {
        var listeId = leggeInnEtSettMedOppgaver();
        leggtilOppgaveMedEkstraEgenskaper(førstegangBehandlingVent, AndreKriterierType.TIL_BESLUTTER);
        leggtilOppgaveMedEkstraEgenskaper(klageOppgave, AndreKriterierType.TIL_BESLUTTER);
        leggtilOppgaveMedEkstraEgenskaper(klageOppgave, AndreKriterierType.NÆRING);

        var liste = oppgaveRepository.hentOppgaveFilterSett(listeId).orElseThrow();
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(),
            Set.of(),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(AndreKriterierType.TIL_BESLUTTER), Set.of())
        );
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);
        var oppgaver = hentAntallOppgaver(listeId);
        assertThat(oppgaver).isEqualTo(2);


        var sakslisteNæringEksludert = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(),
            Set.of(),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(AndreKriterierType.TIL_BESLUTTER), Set.of(AndreKriterierType.NÆRING))
        );
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(sakslisteNæringEksludert);
        oppgaver = hentAntallOppgaver(listeId);
        assertThat(oppgaver).isEqualTo(1);
    }

    @Test
    void testUtenFiltreringpåYtelseTypeype() {
        var listeId = leggeInnEtSettMedOppgaver();
        var liste = oppgaveRepository.hentOppgaveFilterSett(listeId).orElseThrow();
        var saksliste = new SakslisteLagreDto(
            liste.getAvdeling().getAvdelingEnhet(),
            liste.getId(),
            liste.getNavn(),
            new SakslisteLagreDto.SorteringDto(liste.getSortering(), Periodefilter.FAST_PERIODE, null, null, null, null),
            Set.of(),
            Set.of(FagsakYtelseType.ENGANGSTØNAD),
            new SakslisteLagreDto.AndreKriterieDto(Set.of(AndreKriterierType.TIL_BESLUTTER), Set.of())
        );
        avdelingslederTjeneste.endreEksistrendeOppgaveFilter(saksliste);
        var oppgaver = hentAntallOppgaver(listeId);
        assertThat(oppgaver).isZero();
    }

    @Test
    void hentAntallOppgaver() {
        var oppgaveFiltreringId = leggeInnEtSettMedOppgaver();
        var oppgaver = hentAntallOppgaver(oppgaveFiltreringId);
        assertThat(oppgaver).isEqualTo(3);
    }


    private Integer hentAntallOppgaver(Long behandlingsKø) {
        var queryDto = oppgaveRepository.hentOppgaveFilterSett(behandlingsKø)
            .map(of -> new Oppgavespørring(of, Filtreringstype.ALLE))
            .orElseThrow(() -> new FunksjonellException("FP-164687", "Fant ikke oppgavekø med id " + behandlingsKø));
        return behandlingKøRepository.hentAntallBehandlingerPåVent(queryDto);
    }



    private void leggtilOppgaveMedEkstraEgenskaper(Behandling behandling, AndreKriterierType andreKriterierType) {
        oppgaveRepository.nyeBehandlingEgenskaper(behandling.getId(), Set.of(andreKriterierType));
    }


    private Long leggeInnEtSettMedOppgaver() {
        var oppgaveFiltrering = new OppgaveFiltrering();
        oppgaveFiltrering.setNavn("OPPRETTET");
        oppgaveFiltrering.setSortering(KøSortering.OPPRETT_BEHANDLING);
        oppgaveFiltrering.setAvdeling(avdelingDrammen(entityManager));
        oppgaveRepository.lagre(oppgaveFiltrering);
        oppgaveRepository.lagre(førstegangBehandlingAksjonspunkt);
        oppgaveRepository.lagre(førstegangBehandlingVent);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
        oppgaveRepository.lagre(førstegangOppgaveBergen);
        entityManager.refresh(oppgaveFiltrering);
        return oppgaveFiltrering.getId();
    }

}
