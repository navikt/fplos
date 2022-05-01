package no.nav.foreldrepenger.los.web.server.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.jaspi.DefaultAuthConfigFactory;
import org.eclipse.jetty.security.jaspi.JaspiAuthenticatorFactory;
import org.eclipse.jetty.security.jaspi.provider.JaspiAuthConfigProvider;
import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.MDC;

import no.nav.vedtak.sikkerhet.jaspic.OidcAuthModule;

abstract class AbstractJettyServer {

    /**
     * @see AbstractNetworkConnector#getHost()
     */
    static final class ResetLogContextHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request,
                           HttpServletResponse response) {
            MDC.clear();
        }
    }

    private AppKonfigurasjon appKonfigurasjon;

    public AbstractJettyServer(AppKonfigurasjon appKonfigurasjon) {
        this.appKonfigurasjon = appKonfigurasjon;
    }


    protected void bootStrap() throws Exception {
        konfigurer();
        migrerDatabaser();
        start(appKonfigurasjon);
    }

    protected void konfigurer() throws Exception {
        konfigurerMiljø();
        konfigurerSikkerhet();
        konfigurerJndi();
    }

    protected abstract void konfigurerMiljø();

    protected void konfigurerSikkerhet() {
        var factory = new DefaultAuthConfigFactory();
        factory.registerConfigProvider(new JaspiAuthConfigProvider(new OidcAuthModule()),
                "HttpServlet",
                "server " + appKonfigurasjon.getContextPath(),
                "OIDC Authentication");

        AuthConfigFactory.setFactory(factory);
    }

    protected abstract void konfigurerJndi() throws Exception;

    protected abstract void migrerDatabaser();

    protected void start(AppKonfigurasjon appKonfigurasjon) throws Exception {
        var server = new Server(appKonfigurasjon.getServerPort());
        server.setConnectors(createConnectors(appKonfigurasjon, server).toArray(new Connector[]{}));
        var handlers = new HandlerList(new ResetLogContextHandler(), createContext(appKonfigurasjon));
        server.setHandler(handlers);
        server.start();
        server.join();
    }

    @SuppressWarnings("resource")
    protected List<Connector> createConnectors(AppKonfigurasjon appKonfigurasjon, Server server) {
        List<Connector> connectors = new ArrayList<>();
        var httpConnector = new ServerConnector(server, new HttpConnectionFactory(createHttpConfiguration()));
        httpConnector.setPort(appKonfigurasjon.getServerPort());
        connectors.add(httpConnector);

        return connectors;
    }

    protected WebAppContext createContext(AppKonfigurasjon appKonfigurasjon) throws IOException {
        var webAppContext = new WebAppContext();
        webAppContext.setParentLoaderPriority(true);

        // må hoppe litt bukk for å hente web.xml fra classpath i stedet for fra filsystem.
        String descriptor;
        try (var resource = Resource.newClassPathResource("/WEB-INF/web.xml")) {
            descriptor = resource.getURI().toURL().toExternalForm();
        }
        webAppContext.setDescriptor(descriptor);
        webAppContext.setBaseResource(createResourceCollection());
        webAppContext.setContextPath(appKonfigurasjon.getContextPath());
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", "^.*jersey-.*.jar$|^.*felles-.*.jar$");
        webAppContext.setAttribute("org.eclipse.jetty.annotations.multiThreaded", false);
        webAppContext.setSecurityHandler(createSecurityHandler());
        return webAppContext;
    }


    protected HttpConfiguration createHttpConfiguration() {
        // Create HTTP Config
        var httpConfig = new HttpConfiguration();

        // Add support for X-Forwarded headers
        httpConfig.addCustomizer( new org.eclipse.jetty.server.ForwardedRequestCustomizer() );
        httpConfig.setRequestHeaderSize(16384); // øker for å unngå 431 error code

        return httpConfig;

    }

    protected abstract Resource createResourceCollection() throws IOException;

    private SecurityHandler createSecurityHandler() {
        var securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticatorFactory(new JaspiAuthenticatorFactory());

        var loginService = new JAASLoginService();
        loginService.setName("jetty-login");
        loginService.setLoginModuleName("jetty-login");
        loginService.setIdentityService(new DefaultIdentityService());
        securityHandler.setLoginService(loginService);

        return securityHandler;
    }
}
