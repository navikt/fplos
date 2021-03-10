package no.nav.foreldrepenger.los.web.server.jetty;

import static no.nav.foreldrepenger.dbstoette.Databaseskjemainitialisering.migrer;
import static no.nav.foreldrepenger.dbstoette.Databaseskjemainitialisering.settJndiOppslag;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
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
    private static final String TRUSTSTORE_PASSW_PROP = "javax.net.ssl.trustStorePassword";
    private static final String TRUSTSTORE_PATH_PROP = "javax.net.ssl.trustStore";
    private static final String KEYSTORE_PASSW_PROP = "no.nav.modig.security.appcert.password";
    private static final String KEYSTORE_PATH_PROP = "no.nav.modig.security.appcert.keystore";

    public static void main(String[] args) throws Exception {
        JettyDevServer devServer = new JettyDevServer();
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

        // truststore avgjør hva vi stoler på av sertifikater når vi gjør utadgående TLS kall
        initCryptoStoreConfig("truststore", TRUSTSTORE_PATH_PROP, TRUSTSTORE_PASSW_PROP, "changeit");

        // keystore genererer sertifikat og TLS for innkommende kall. Bruker standard prop hvis definert, ellers faller tilbake på modig props
        var keystoreProp = System.getProperty("javax.net.ssl.keyStore") != null ? "javax.net.ssl.keyStore" : KEYSTORE_PATH_PROP;
        var keystorePasswProp = System.getProperty("javax.net.ssl.keyStorePassword") != null ? "javax.net.ssl.keyStorePassword" : KEYSTORE_PASSW_PROP;
        initCryptoStoreConfig("keystore", keystoreProp, keystorePasswProp, "devillokeystore1234");
    }

    private static String initCryptoStoreConfig(String storeName, String storeProperty, String storePasswordProperty, String defaultPassword) {
        String defaultLocation = getProperty("user.home", ".") + "/.modig/" + storeName + ".jks";
        String storePath = getProperty(storeProperty, defaultLocation);
        File storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke " + storeName + " i " + storePath
                    + "\n\tKonfigurer enten som System property \'" + storeProperty + "\' eller environment variabel \'"
                    + storeProperty.toUpperCase().replace('.', '_') + "\'");
        }
        String password = getProperty(storePasswordProperty, defaultPassword);
        if (password == null) {
            throw new IllegalStateException("Passord for å aksessere store " + storeName + " i " + storePath + " er null");
        }

        System.setProperty(storeProperty, storeFile.getAbsolutePath());
        System.setProperty(storePasswordProperty, password);
        return storePath;
    }

    private static String getProperty(String key, String defaultValue) {
        String val = System.getProperty(key, defaultValue);
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
        List<Connector> connectors = super.createConnectors(appKonfigurasjon, server);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(System.getProperty("no.nav.modig.security.appcert.keystore"));
        sslContextFactory.setKeyStorePassword(System.getProperty("no.nav.modig.security.appcert.password"));
        sslContextFactory.setKeyManagerPassword(System.getProperty("no.nav.modig.security.appcert.password"));

        HttpConfiguration https = createHttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(appKonfigurasjon.getSslPort());
        connectors.add(sslConnector);

        return connectors;
    }

    @Override
    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        WebAppContext webAppContext = super.createContext(appKonfigurasjon);
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
