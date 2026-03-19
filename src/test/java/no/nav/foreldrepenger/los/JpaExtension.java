package no.nav.foreldrepenger.los;


import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareExtension;

public class JpaExtension extends EntityManagerAwareExtension {
    public static final String DEFAULT_TEST_DB_SCHEMA_NAME;
    private static final String TEST_DB_CONTAINER = Environment.current().getProperty("testcontainer.test.db", String.class, "postgres:18-alpine");
    private static final PostgreSQLContainer<?> TEST_DATABASE;

    static {
        TEST_DATABASE = new PostgreSQLContainer<>(DockerImageName.parse(TEST_DB_CONTAINER)).withReuse(true);
        TEST_DATABASE.start();
        DEFAULT_TEST_DB_SCHEMA_NAME = TEST_DATABASE.getUsername();
        TestDatabaseInit.settOppDatasourceOgMigrer(TEST_DATABASE.getJdbcUrl(), DEFAULT_TEST_DB_SCHEMA_NAME, TEST_DATABASE.getPassword());
    }
}
