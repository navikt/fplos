package no.nav.foreldrepenger.los.web.server.jetty;

public class JettyDevKonfigurasjon extends JettyWebKonfigurasjon {

    JettyDevKonfigurasjon(){
        super(8071);
    }

    @Override
    public int getSslPort() {
        return 8445;
    }

}
