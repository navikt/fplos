package no.nav.foreldrepenger.los.web.server.jetty;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import no.nav.vedtak.util.env.Environment;

class DataSourceKonfig {

    private static final Environment ENV = Environment.current();

    private final DBConnProp defaultDS;

    DataSourceKonfig() {
        var defaultDSName = "defaultDS";
        this.defaultDS = new DBConnProp(ds(defaultDSName), defaultDSName);
    }

    private static DataSource ds(String dataSourceName) {
        var config = new HikariConfig();
        config.setJdbcUrl(ENV.getProperty(dataSourceName + ".url"));
        config.setUsername(ENV.getProperty(dataSourceName + ".username"));
        config.setPassword(ENV.getProperty(dataSourceName + ".password"));

        config.setConnectionTimeout(1000);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(30);
        config.setConnectionTestQuery("select 1 from dual");
        config.setDriverClassName("oracle.jdbc.OracleDriver");

        Properties dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);

        return new HikariDataSource(config);
    }

    public DBConnProp defaultDS() {
        return defaultDS;
    }
}
