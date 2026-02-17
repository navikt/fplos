package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.DBTestUtil.hentAlle;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.BEHANDLINGSFRIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class OrganisasjonRepositoryTest {
    private OrganisasjonRepository repository;
    private OppgaveRepository oppgaveRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        repository = new OrganisasjonRepository(entityManager);
        oppgaveRepository = new OppgaveRepository(entityManager);
    }

    @Test
    void deaktiverAvdeling() {
        var enhetsNummer = "4802";
        var avdelinger = repository.hentAktiveAvdelinger().stream().map(Avdeling::getAvdelingEnhet);

        repository.deaktiverAvdeling(enhetsNummer);
        entityManager.flush(); // håndteres av transaksjon i rammeverk

        var etterDeaktiver = repository.hentAktiveAvdelinger().stream().map(Avdeling::getAvdelingEnhet);

        assertThat(avdelinger).contains(enhetsNummer);
        assertThat(etterDeaktiver).isNotEmpty().doesNotContain(enhetsNummer);
    }

    @Test
    void skalSletteSaksbehandlereUtenKnytninger() {
        var saksbehandlerUtenKnytning = new Saksbehandler("ikke-knyttet", UUID.randomUUID(), "Navn Navnesen", "1234");
        entityManager.persist(saksbehandlerUtenKnytning);

        var saksbehandlerMedKnytning = new Saksbehandler("knyttet", UUID.randomUUID(), "Navn2 Navnesen", "1234");
        entityManager.persist(saksbehandlerMedKnytning);
        repository.tilknyttAvdelingSaksbehandler(avdelingDrammen(entityManager), saksbehandlerMedKnytning);
        entityManager.flush();

        repository.slettSaksbehandlereUtenKnytninger();

        var saksbehandlereEtterSletting = hentAlle(entityManager, Saksbehandler.class);
        assertThat(saksbehandlereEtterSletting)
            .hasSize(1)
            .first().extracting(Saksbehandler::getSaksbehandlerIdent).isEqualTo("knyttet");
    }

    @Test
    void skalFjerneSaksbehandlerUtenNavn() {
        var avdeling = avdelingDrammen(entityManager);
        var avdelingNasjonal = hentAlle(entityManager, Avdeling.class).stream().filter(a -> "4867".equals(a.getAvdelingEnhet())).findAny().orElseThrow();

        var saksbehandlerMedNavn = new Saksbehandler("MNAVN", UUID.randomUUID(), "Navn Navnesen", "1234");
        entityManager.persist(saksbehandlerMedNavn);
        repository.tilknyttAvdelingSaksbehandler(avdeling, saksbehandlerMedNavn);
        repository.tilknyttAvdelingSaksbehandler(avdelingNasjonal, saksbehandlerMedNavn);

        var saksbehandlerUtenNavn = new Saksbehandler("UNAVN", null, null, null);
        entityManager.persist(saksbehandlerUtenNavn);
        repository.tilknyttAvdelingSaksbehandler(avdeling, saksbehandlerUtenNavn);
        repository.tilknyttAvdelingSaksbehandler(avdelingNasjonal, saksbehandlerUtenNavn);

        var gruppe = new SaksbehandlerGruppe("Gruppe1");
        gruppe.setAvdeling(avdelingNasjonal);
        entityManager.persist(gruppe);
        repository.tilknyttGruppeSaksbehandler(gruppe, saksbehandlerMedNavn);
        repository.tilknyttGruppeSaksbehandler(gruppe, saksbehandlerUtenNavn);

        var ofilter = new OppgaveFiltrering();
        ofilter.setNavn("BEHANDLINGSFRIST");
        ofilter.setSortering(BEHANDLINGSFRIST);
        ofilter.setAvdeling(avdelingNasjonal);
        entityManager.persist(ofilter);
        oppgaveRepository.tilknyttSaksbehandlerOppgaveFiltrering(saksbehandlerUtenNavn, ofilter);
        oppgaveRepository.tilknyttSaksbehandlerOppgaveFiltrering(saksbehandlerMedNavn, ofilter);

        entityManager.flush();

        repository.fjernSaksbehandlereSomHarSluttet();

        var saksbehandlereEtterSletting = hentAlle(entityManager, Saksbehandler.class);
        assertThat(saksbehandlereEtterSletting)
            .hasSize(1)
            .satisfies(s -> {
                assertThat(s.getFirst().getSaksbehandlerIdent()).isEqualTo("MNAVN");
                assertThat(oppgaveRepository.oppgaveFiltreringerForSaksbehandler(s.getFirst())).hasSize(1)
                    .first().extracting(OppgaveFiltrering::getNavn).isEqualTo("BEHANDLINGSFRIST");
                assertThat(repository.avdelingerForSaksbehandler(s.getFirst())).hasSize(2);
            });
        assertThat(oppgaveRepository.saksbehandlereForOppgaveFiltrering(ofilter)).hasSize(1).first().extracting(Saksbehandler::getSaksbehandlerIdent).isEqualTo("MNAVN");
        assertThat(repository.saksbehandlereForGruppe(gruppe)).hasSize(1).first().extracting(Saksbehandler::getSaksbehandlerIdent).isEqualTo("MNAVN");

    }

    @Test
    void skalFjerneLøseGruppeTilknytninger() {
        var avdelingNasjonal = hentAlle(entityManager, Avdeling.class).stream().filter(a -> "4867".equals(a.getAvdelingEnhet())).findAny().orElseThrow();

        var saksbehandler1 = new Saksbehandler("NAVN1", UUID.randomUUID(), "Navn Navnes1", "1234");
        entityManager.persist(saksbehandler1);
        repository.tilknyttAvdelingSaksbehandler(avdelingNasjonal, saksbehandler1);

        var saksbehandler2 = new Saksbehandler("NAVN2", UUID.randomUUID(), "Navn Navnes2", "1234");
        entityManager.persist(saksbehandler2);
        repository.tilknyttAvdelingSaksbehandler(avdelingNasjonal, saksbehandler2);

        var gruppe = new SaksbehandlerGruppe("Gruppe1");
        gruppe.setAvdeling(avdelingNasjonal);
        entityManager.persist(gruppe);
        repository.tilknyttGruppeSaksbehandler(gruppe, saksbehandler1);
        repository.tilknyttGruppeSaksbehandler(gruppe, saksbehandler2);
        entityManager.flush();

        // Start
        assertThat(repository.saksbehandlereForGruppe(gruppe)).hasSize(2);
        // DO nothing
        repository.slettLøseGruppeKnytninger();
        assertThat(repository.saksbehandlereForGruppe(gruppe)).hasSize(2);

        // Slett saksbehandler 1 og kjør sletting av løse gruppe tilknytninger
        repository.fraknyttAvdelingSaksbehandler(avdelingNasjonal, saksbehandler1);
        entityManager.flush();

        // Slett med forventet effekt
        repository.slettLøseGruppeKnytninger();
        assertThat(repository.saksbehandlereForGruppe(gruppe)).hasSize(1)
            .extracting(Saksbehandler::getSaksbehandlerIdent).containsExactly("NAVN2");

    }

}
