package no.nav.foreldrepenger.los.web.server.jetty;

public interface AppKonfigurasjon {
    int DEFAULT_SERVER_PORT = 8080;

    default int getServerPort() {
        return DEFAULT_SERVER_PORT;
    }

    String getContextPath();
}
