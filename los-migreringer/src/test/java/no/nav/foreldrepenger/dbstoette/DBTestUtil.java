package no.nav.foreldrepenger.dbstoette;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import no.nav.vedtak.util.env.Environment;

public final class DBTestUtil {
    private static final boolean isRunningUnderMaven = Environment.current().getProperty("maven.cmd.line.args") != null;

    public static boolean kjøresAvMaven() {
        return isRunningUnderMaven;
    }

    public static <T> List<T> hentAlle(EntityManager entityManager, Class<T> klasse) {
        var criteria = entityManager.getCriteriaBuilder().createQuery(klasse);
        criteria.select(criteria.from(klasse));
        return entityManager.createQuery(criteria).getResultList();
    }

    public static <T> T hentUnik(EntityManager entityManager, Class<T> klasse) {
        var entiteter = hentAlle(entityManager, klasse);
        assertThat(entiteter).hasSize(1);
        return entiteter.get(0);
    }

}
