package no.nav.foreldrepenger.los.web.app.selftest.checks;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.integrasjon.arbeidsfordeling.klient.ArbeidsfordelingSelftestConsumer;

@ApplicationScoped
public class ArbeidsfordelingWebServiceHealthCheck extends WebServiceHealthCheck {

    private ArbeidsfordelingSelftestConsumer selftestConsumer;

    ArbeidsfordelingWebServiceHealthCheck() {
        // for CDI
    }

    @Inject
    public ArbeidsfordelingWebServiceHealthCheck(ArbeidsfordelingSelftestConsumer selftestConsumer) {
        this.selftestConsumer = selftestConsumer;
    }

    @Override
    protected void performWebServiceSelftest() {
        selftestConsumer.ping();
    }

    @Override
    protected String getDescription() {
        return "Test av web service Arbeidsfordeling";
    }

    @Override
    protected String getEndpoint() {
        return selftestConsumer.getEndpointUrl();
    }

    @Override
    public boolean erKritiskTjeneste() {
        return true;
    }
}
