package no.nav.foreldrepenger.los.web.server.jetty;

import no.nav.vedtak.konfig.PropertiesKonfigVerdiProvider;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Properties;

@ApplicationScoped
public class ApplicationPropertiesKonfigVerdiProvider extends PropertiesKonfigVerdiProvider {

    protected ApplicationPropertiesKonfigVerdiProvider() {
        super(initApplicationProperties());
    }

    static Properties initApplicationProperties() {
        String appPropertyFile = "/application.properties";
        try (var is = JettyServer.class.getResourceAsStream(appPropertyFile);) {
            Properties appProps = new Properties();
            appProps.load(is);
            return appProps;
        } catch (IOException e) {
            throw new IllegalStateException("Kunne ikke laste props fra " + appPropertyFile); //$NON-NLS-1$
        }
    }

    @Override
    public int getPrioritet() {
        return 999; // Lav prioritet (enn system props, etc)
    }

}
