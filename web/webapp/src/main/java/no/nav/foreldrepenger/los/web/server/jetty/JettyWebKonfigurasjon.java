package no.nav.foreldrepenger.los.web.server.jetty;

import no.nav.vedtak.sikkerhet.ContextPathHolder;

public class JettyWebKonfigurasjon implements AppKonfigurasjon {
    private static final String CONTEXT_PATH = "/fplos";

    private Integer serverPort;

    public JettyWebKonfigurasjon() {
        ContextPathHolder.instance(CONTEXT_PATH);
    }

    public JettyWebKonfigurasjon(int serverPort) {
        this();
        this.serverPort = serverPort;
    }

    @Override
    public int getServerPort() {
        if (serverPort == null) {
            return AppKonfigurasjon.DEFAULT_SERVER_PORT;
        }
        return serverPort;
    }

    @Override
    public String getContextPath() {
        return CONTEXT_PATH;
    }
}
