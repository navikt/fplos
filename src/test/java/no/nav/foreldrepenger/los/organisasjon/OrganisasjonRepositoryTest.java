package no.nav.foreldrepenger.los.organisasjon;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
}
