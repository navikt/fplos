package no.nav.foreldrepenger.los.web.app.startupinfo;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.selftest.Selftests;
import no.nav.vedtak.log.mdc.MDCOperations;

/**
 * Dependent scope siden vi lukker denne når vi er ferdig.
 */
@Dependent
class AppStartupInfoLogger {

    private static final Logger LOG = LoggerFactory.getLogger(AppStartupInfoLogger.class);

    private final Selftests selftests;

    private static final String OPPSTARTSINFO = "OPPSTARTSINFO";
    private static final String HILITE_SLUTT = "********";
    private static final String HILITE_START = HILITE_SLUTT;
    private static final String SELFTEST = "Selftest";
    private static final String APPLIKASJONENS_STATUS = "Applikasjonens status";
    private static final String START = "start:";
    private static final String SLUTT = "slutt.";

    @Inject
    AppStartupInfoLogger(Selftests selftests) {
        this.selftests = selftests;
    }

    void logAppStartupInfo() {
        log(HILITE_START + " " + OPPSTARTSINFO + " " + START + " " + HILITE_SLUTT);
        logSelftest();
        log(HILITE_START + " " + OPPSTARTSINFO + " " + SLUTT + " " + HILITE_SLUTT);
    }

    private void logSelftest() {
        log(SELFTEST + " " + START);

        // callId er påkrevd på utgående kall og må settes før selftest kjøres
        MDCOperations.putCallId();
        var selftestsResultat = selftests.run();
        MDCOperations.removeCallId();

        log(selftestsResultat);

        log(APPLIKASJONENS_STATUS + ": {}", getStatus(selftestsResultat.isReady()));
        log(SELFTEST + " " + SLUTT);
    }

    private static void log(String msg, Object... args) {
        LOG.info(msg, args);
    }

    private static void log(Selftests.Resultat result) {
        OppstartFeil.FACTORY.selftestStatus(
                getStatus(result.isReady()),
                result.getDescription(),
                result.getEndpoint()).log(LOG);
    }

    private static String getStatus(boolean isHealthy) {
        return isHealthy ? "OK" : "ERROR";
    }
}
