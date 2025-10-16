package no.nav.foreldrepenger.los;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.foreldrepenger.los.server.JettyServer;

public class JettyDevServer extends JettyServer {

    private static final Environment ENV = Environment.current();

    static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    protected static JettyDevServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyDevServer(Integer.parseUnsignedInt(args[0]));
        }
        return new JettyDevServer(ENV.getProperty("server.port", Integer.class, 8071));
    }

    private JettyDevServer(int serverPort) {
        super(serverPort);
    }
}
