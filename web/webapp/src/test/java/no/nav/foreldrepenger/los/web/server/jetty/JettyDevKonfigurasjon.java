package no.nav.foreldrepenger.los.web.server.jetty;

public class JettyDevKonfigurasjon extends JettyWebKonfigurasjon {
    private static final int SSL_SERVER_PORT = 8445;
    private static int DEFAULT_DEV_SERVER_PORT = 8071;

    JettyDevKonfigurasjon(){
        super(8071);
    }

    @Override
    public int getSslPort() {
        return SSL_SERVER_PORT;
    }

}
