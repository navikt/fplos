package no.nav.foreldrepenger.los.organisasjon;

import static no.nav.foreldrepenger.los.DBTestUtil.avdelingDrammen;
import static no.nav.foreldrepenger.los.DBTestUtil.hentAlle;
import static no.nav.foreldrepenger.los.oppgavekø.KøSortering.BEHANDLINGSFRIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveFiltrering;

@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class OrganisasjonRepositoryTest {
    private OrganisasjonRepository repository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        repository = new OrganisasjonRepository(entityManager);
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
        saksbehandlerMedKnytning.leggTilAvdeling(avdelingDrammen(entityManager));
        entityManager.persist(saksbehandlerMedKnytning);
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

        var saksbehandlerUtenNavn = new Saksbehandler("MNAVN", UUID.randomUUID(), "Navn Navnesen", "1234");
        saksbehandlerUtenNavn.leggTilAvdeling(avdeling);
        saksbehandlerUtenNavn.leggTilAvdeling(avdelingNasjonal);
        entityManager.persist(saksbehandlerUtenNavn);

        var saksbehandlerMedNavn = new Saksbehandler("UNAVN", null, null, null);
        saksbehandlerMedNavn.leggTilAvdeling(avdeling);
        saksbehandlerMedNavn.leggTilAvdeling(avdelingNasjonal);

        entityManager.persist(saksbehandlerMedNavn);

        var gruppe = new SaksbehandlerGruppe("Gruppe1");
        gruppe.setAvdeling(avdelingNasjonal);
        gruppe.getSaksbehandlere().addAll(Set.of(saksbehandlerUtenNavn, saksbehandlerMedNavn));
        entityManager.persist(gruppe);

        var ofilter = new OppgaveFiltrering();
        ofilter.setNavn("BEHANDLINGSFRIST");
        ofilter.setSortering(BEHANDLINGSFRIST);
        ofilter.setAvdeling(avdelingNasjonal);
        ofilter.leggTilSaksbehandler(saksbehandlerMedNavn);
        ofilter.leggTilSaksbehandler(saksbehandlerUtenNavn);
        entityManager.persist(ofilter);

        entityManager.flush();
        // Last inn oppgavefiltrering og saksbehandlere på nytt for å sikre at vi har siste versjon før sletting
        entityManager.refresh(saksbehandlerUtenNavn);
        entityManager.refresh(saksbehandlerMedNavn);

        repository.fjernSaksbehandlereSomHarSluttet();

        var saksbehandlereEtterSletting = hentAlle(entityManager, Saksbehandler.class);
        assertThat(saksbehandlereEtterSletting)
            .hasSize(1)
            .satisfies(s -> {
                assertThat(s.getFirst().getSaksbehandlerIdent()).isEqualTo("MNAVN");
                assertThat(s.getFirst().getOppgaveFiltreringer()).hasSize(1)
                    .first().extracting(OppgaveFiltrering::getNavn).isEqualTo("BEHANDLINGSFRIST");
                assertThat(s.getFirst().getAvdelinger()).hasSize(2);
            });
        entityManager.refresh(ofilter);
        assertThat(ofilter.getSaksbehandlere()).hasSize(1).first().extracting(Saksbehandler::getSaksbehandlerIdent).isEqualTo("MNAVN");
        entityManager.refresh(gruppe);
        assertThat(gruppe.getSaksbehandlere()).hasSize(1).first().extracting(Saksbehandler::getSaksbehandlerIdent).isEqualTo("MNAVN");

    }

}
