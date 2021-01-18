package no.nav.foreldrepenger.los.web.server.jetty;

import no.nav.foreldrepenger.los.web.app.ApplicationConfig;
import no.nav.foreldrepenger.los.web.server.jetty.DataSourceKonfig.DBConnProp;
import no.nav.vedtak.isso.IssoApplication;
import no.nav.vedtak.util.env.Environment;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JettyServer extends AbstractJettyServer {

    private DataSourceKonfig dataSourceKonfig;

    public JettyServer() {
        this(new JettyWebKonfigurasjon());
    }

    public JettyServer(int serverPort) {
        this(new JettyWebKonfigurasjon(serverPort));
    }

    JettyServer(AppKonfigurasjon appKonfigurasjon) {
        super(appKonfigurasjon);
    }

    public static void main(String[] args) throws Exception {
        JettyServer jettyServer;
        if (args.length > 0) {
            int serverPort = Integer.parseUnsignedInt(args[0]);
            jettyServer = new JettyServer(serverPort);
        } else {
            jettyServer = new JettyServer();
        }
        jettyServer.bootStrap();
    }

    @Override
    protected void konfigurerMiljø() throws Exception {
        dataSourceKonfig = new DataSourceKonfig();
    }

    @Override
    protected void konfigurerJndi() throws Exception {
        new EnvEntry("jdbc/defaultDS", dataSourceKonfig.getDefaultDatasource().getDatasource());
    }


    @Override
    protected void migrerDatabaser() throws IOException {
        boolean cleanOnException = !Environment.current().isProd();
        for (DBConnProp dbConnProp : dataSourceKonfig.getDataSources()) {
            new DatabaseScript(dbConnProp.getDatasource(), cleanOnException, dbConnProp.getMigrationScripts()).migrate();
        }
    }

    @Override
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        WebAppContext webAppContext = super.createContext(appKonfigurasjon);
        webAppContext.setParentLoaderPriority(true);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        List<Class<?>> appClasses = Arrays.asList((Class<?>) ApplicationConfig.class, (Class<?>)IssoApplication.class);

        List<Resource> resources = appClasses.stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .distinct()
                .collect(Collectors.toList());

        metaData.setWebInfClassesDirs(resources);
    }

    @Override
    protected ResourceCollection createResourceCollection() throws IOException {
        return new ResourceCollection(
                Resource.newResource(System.getProperty("klient", "./klient")),
                Resource.newClassPathResource("/META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"),
                Resource.newClassPathResource("/META-INF/resources")/** i18n */);
    }

}
