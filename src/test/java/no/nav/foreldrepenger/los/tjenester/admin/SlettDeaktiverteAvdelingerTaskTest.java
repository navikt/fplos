package no.nav.foreldrepenger.los.tjenester.admin;

import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.DBTestUtil.hentAlle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederSaksbehandlerTjeneste;
import no.nav.foreldrepenger.los.avdelingsleder.AvdelingslederTjeneste;
import no.nav.foreldrepenger.los.oppgave.OppgaveKøRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.KøSortering;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;
import no.nav.foreldrepenger.los.organisasjon.Avdeling;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.Saksbehandler;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.BrukerProfil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class SlettDeaktiverteAvdelingerTaskTest {
    private EntityManager entityManager;
    private SlettDeaktiverteAvdelingerTask task;
    private AvdelingslederSaksbehandlerTjeneste avdelingslederSaksbehandlerTjeneste;
    private OrganisasjonRepository organisasjonRepository;
    private OppgaveRepository oppgaveRepository;
    @Mock
    private AnsattTjeneste ansattTjeneste;

    @BeforeEach
    void setup(EntityManager entityManager) {
        this.entityManager = entityManager;
        organisasjonRepository = new OrganisasjonRepository(entityManager);
        var oppgaveKøRepository = new OppgaveKøRepository(entityManager);
        oppgaveRepository = new OppgaveRepository(entityManager);
        var avdelingslederTjeneste = new AvdelingslederTjeneste(oppgaveRepository, organisasjonRepository);
        lenient().when(ansattTjeneste.hentBrukerProfil(anyString())).thenReturn(Optional.of(new BrukerProfil(UUID.randomUUID(), "A000001", "Ansatt Navn", "4867")));
        this.avdelingslederSaksbehandlerTjeneste = new AvdelingslederSaksbehandlerTjeneste(oppgaveRepository, organisasjonRepository, ansattTjeneste);
        task = new SlettDeaktiverteAvdelingerTask(oppgaveKøRepository, organisasjonRepository, avdelingslederTjeneste,
            avdelingslederSaksbehandlerTjeneste);
    }

    @Test
    void skalSletteAvdeling() {
        var avdeling = avdelingDrammen(entityManager);

        var avdelingEnhetsnummer = avdeling.getAvdelingEnhet();
        var køDefinisjon = new OppgaveFiltrering();
        køDefinisjon.setNavn("OPPRETTET");
        køDefinisjon.setSortering(KøSortering.OPPRETT_BEHANDLING);
        køDefinisjon.setAvdeling(avdeling);

        avdelingslederSaksbehandlerTjeneste.leggSaksbehandlerTilAvdeling("saksbeh", avdelingEnhetsnummer);
        entityManager.flush();
        var saksbehandlere = avdelingslederSaksbehandlerTjeneste.hentAvdelingensSaksbehandlere(avdelingEnhetsnummer);

        avdeling.setErAktiv(false);

        entityManager.persist(køDefinisjon);
        entityManager.persist(avdeling);
        entityManager.flush();
        oppgaveRepository.tilknyttSaksbehandlerOppgaveFiltrering(saksbehandlere.getFirst(), køDefinisjon);
        entityManager.flush();
        entityManager.clear();

        var parametre = ProsessTaskData.forProsessTask(SlettDeaktiverteAvdelingerTask.class);
        parametre.setProperty(SlettDeaktiverteAvdelingerTask.ENHETSNR, avdelingEnhetsnummer);
        task.doTask(parametre);
        entityManager.flush();

        assertThat(hentAlle(entityManager, OppgaveFiltrering.class)).isEmpty();
        assertThat(hentAlle(entityManager, Saksbehandler.class))
            .flatExtracting(organisasjonRepository::avdelingerForSaksbehandler)
            .isEmpty();
        assertThat(hentAlle(entityManager, Avdeling.class))
            .extracting(Avdeling::getAvdelingEnhet)
            .doesNotContain(avdelingEnhetsnummer);
    }

    @Test
    void skalAvbryteVedFortsattAktivAvdeling() {
        var avdeling = avdelingDrammen(entityManager);
        var avdelingEnhetsnummer = avdeling.getAvdelingEnhet();

        var parametre = ProsessTaskData.forProsessTask(SlettDeaktiverteAvdelingerTask.class);
        parametre.setProperty(SlettDeaktiverteAvdelingerTask.ENHETSNR, avdelingEnhetsnummer);
        task.doTask(parametre);
        entityManager.flush();

        assertThat(avdelingDrammen(entityManager)).isNotNull();
    }



}
