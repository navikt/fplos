package no.nav.foreldrepenger.dbstoette;

import static no.nav.foreldrepenger.dbstoette.DBTestUtil.kjøresAvMaven;

import java.io.File;

import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import no.nav.vedtak.util.env.Environment;

/**
 * Initielt skjemaoppsett + migrering av unittest-skjemaer
 */
public final class Databaseskjemainitialisering {

    private static final Logger LOG = LoggerFactory.getLogger(Databaseskjemainitialisering.class);
    private static final Environment ENV = Environment.current();
    public static final String URL_DEFAULT = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp) "
            + "(HOST=0.0.0.0)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=XE)))";
    public static final String DEFAULT_SCEHMA = "fplos";
    public static final String JUNIT_SCHEMA = "fplos_unit";
    public static final String DBA_SCHEMA = "vl_dba";
    public static final String DEFAULT_DS_NAME = "defaultDS";
    public static final String DBA_DS_NAME = "vl_dba";

    public static final DBProperties DBA_PROPERTIES = new DBProperties(DBA_DS_NAME, DBA_SCHEMA, dbaScriptLocation());
    public static final DBProperties JUNIT_PROPERTIES = dbProperties(DEFAULT_DS_NAME, JUNIT_SCHEMA);
    public static final DBProperties DEFAULT_PROPERTIES = dbProperties(DEFAULT_DS_NAME, DEFAULT_SCEHMA);

    public static void main(String[] args) {
        //brukes i mvn clean install
        migrerForUnitTests();
    }

    public static void migrer() {
        migrer(DEFAULT_PROPERTIES);
    }

    public static void migrerForUnitTests() {
        //Må kjøres først for å opprette fplos_unit
        migrer(DBA_PROPERTIES);
        migrer(JUNIT_PROPERTIES);
    }

    public static DBProperties dbProperties(String dsName, String schema) {
        return new DBProperties(dsName, schema, getScriptLocation(dsName));
    }

    public static void settJndiOppslag() {
        settJndiOppslag(DEFAULT_PROPERTIES);
    }

    public static void settJndiOppslagForUnitTests() {
        settJndiOppslag(JUNIT_PROPERTIES);
    }

    private static void settJndiOppslag(DBProperties properties) {
        try {
            var props = properties;
            new EnvEntry("jdbc/" + props.dsName(), props.dataSource());
        } catch (Exception e) {
            throw new RuntimeException("Feil under registrering av Jndi-entry for default datasource", e);
        }
    }

    private static void migrer(DBProperties dbProperties) {
        LOG.info("Migrerer {}", dbProperties.schema());
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dbProperties.dataSource());
        flyway.setTable("schema_version");
        flyway.setLocations(dbProperties.scriptLocation());
        flyway.setCleanOnValidationError(true);
        if (!ENV.isLocal()) {
            throw new IllegalStateException("Forventer at denne migreringen bare kjøres lokalt");
        }
        flyway.migrate();
    }

    private static String getScriptLocation(String dsName) {
        if (kjøresAvMaven()) {
            return classpathScriptLocation(dsName);
        }
        return fileScriptLocation(dsName);
    }

    private static String classpathScriptLocation(String dsName) {
        return "classpath:/db/migration/" + dsName;
    }

    private static String dbaScriptLocation() {
        if (kjøresAvMaven()) {
            return classpathScriptLocation("vl_dba");
        }
        return "migreringer/src/test/resources/db/migration/vl_dba";
    }

    private static String fileScriptLocation(String dsName) {
        String relativePath = "migreringer/src/main/resources/db/migration/" + dsName;

        File baseDir = new File(".").getAbsoluteFile();
        File location = new File(baseDir, relativePath);
        while (!location.exists()) {
            baseDir = baseDir.getParentFile();
            if (baseDir == null || !baseDir.isDirectory()) {
                throw new IllegalArgumentException("Klarte ikke finne : " + baseDir);
            }
            location = new File(baseDir, relativePath);
        }
        return "filesystem:" + location.getPath();
    }

    private static HikariConfig hikariConfig(String dsName, String schema) {
        var cfg = new HikariConfig();
        cfg.setJdbcUrl(ENV.getProperty(dsName + ".url", URL_DEFAULT));
        cfg.setUsername(ENV.getProperty(dsName + ".username", schema));
        cfg.setPassword(ENV.getProperty(dsName + ".password", schema));
        cfg.setConnectionTimeout(10000);
        cfg.setMinimumIdle(0);
        cfg.setMaximumPoolSize(4);
        cfg.setAutoCommit(false);
        return cfg;
    }
    public static class DBProperties {

        private final String schema;
        private final String scriptLocation;
        private final String dsName;
        private HikariDataSource ds;

        private DBProperties(String dsName, String schema, String scriptLocation) {
            this.dsName = dsName;
            this.schema = schema;
            this.scriptLocation = scriptLocation;
        }

        public String dsName() {
            return dsName;
        }

        public String schema() {
            return schema;
        }

        public synchronized DataSource dataSource() {
            if (ds == null) {
                ds = new HikariDataSource(hikariConfig(dsName, schema));
                Runtime.getRuntime().addShutdownHook(new Thread(() -> ds.close()));
            }
            return ds;
        }

        public String scriptLocation() {
            return scriptLocation;
        }

        @Override
        public String toString() {
            return "DBProperties{" + "schema='" + schema + '\'' + ", scriptLocation='" + scriptLocation + '\''
                    + ", dsName='" + dsName + '\'' + '}';
        }
    }
}
