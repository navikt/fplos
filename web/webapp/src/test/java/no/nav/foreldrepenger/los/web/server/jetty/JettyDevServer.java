package no.nav.foreldrepenger.los.web.server.jetty;

import static no.nav.foreldrepenger.dbstøtte.Databaseskjemainitialisering.migrer;
import static no.nav.foreldrepenger.dbstøtte.Databaseskjemainitialisering.settJndiOppslag;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyDevServer extends JettyServer {

    /**
     * @link https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html
     */

    public static void main(String[] args) throws Exception {
        var devServer = new JettyDevServer();
        devServer.bootStrap();
    }

    public JettyDevServer() {
        super(new JettyDevKonfigurasjon());
    }

    @Override
    protected void konfigurerMiljø() {

    }

    @Override
    protected void konfigurerSikkerhet() {
        System.setProperty("conf", "src/main/resources/jetty/");
        super.konfigurerSikkerhet();

        initCryptoStoreConfig("truststore", "javax.net.ssl.trustStore", "javax.net.ssl.trustStorePassword", "changeit");
        initCryptoStoreConfig("keystore", "javax.net.ssl.keyStore", "javax.net.ssl.keyStorePassword", "devillokeystore1234");
    }

    private static String initCryptoStoreConfig(String storeName, String storeProperty, String storePasswordProperty, String defaultPassword) {
        var defaultLocation = getProperty("user.home", ".") + "/.modig/" + storeName + ".jks";
        var storePath = getProperty(storeProperty, defaultLocation);
        var storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke " + storeName + " i " + storePath
                    + "\n\tKonfigurer enten som System property \'" + storeProperty + "\' eller environment variabel \'"
                    + storeProperty.toUpperCase().replace('.', '_') + "\'");
        }
        var password = getProperty(storePasswordProperty, defaultPassword);
        if (password == null) {
            throw new IllegalStateException("Passord for å aksessere store " + storeName + " i " + storePath + " er null");
        }

        System.setProperty(storeProperty, storeFile.getAbsolutePath());
        System.setProperty(storePasswordProperty, password);
        return storePath;
    }

    private static String getProperty(String key, String defaultValue) {
        var val = System.getProperty(key, defaultValue);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
            val = val == null ? defaultValue : val;
        }
        return val;
    }

    @Override
    protected void konfigurerJndi() {
        settJndiOppslag();
    }

    @Override
    protected void migrerDatabaser() {
        migrer();
    }

    @SuppressWarnings("resource")
    @Override
    protected List<Connector> createConnectors(AppKonfigurasjon appKonfigurasjon, Server server) {
        var connectors = super.createConnectors(appKonfigurasjon, server);

        var sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(System.getProperty("javax.net.ssl.keyStore"));
        sslContextFactory.setKeyStorePassword(System.getProperty("javax.net.ssl.keyStorePassword"));
        sslContextFactory.setKeyManagerPassword(System.getProperty("javax.net.ssl.keyStorePassword"));

        var https = createHttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        var sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(appKonfigurasjon.getSslPort());
        connectors.add(sslConnector);

        return connectors;
    }

    @Override
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        var webAppContext = super.createContext(appKonfigurasjon);
        // https://www.eclipse.org/jetty/documentation/9.4.x/troubleshooting-locked-files-on-windows.html
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        return webAppContext;
    }

    @Override
    protected ResourceCollection createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("/META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"),
                Resource.newClassPathResource("/META-INF/resources")/** i18n */
        );
    }

}
