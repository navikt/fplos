package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.util.env.Cluster;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SaksbehandlerEnhetstilgangTjeneste {

    private static final String PATH = "/api/v1/tilgang/";
    private String host;
    private OidcRestClient httpClient;

    @Inject
    public SaksbehandlerEnhetstilgangTjeneste(@KonfigVerdi(value = "axsys.url", defaultVerdi = "http://axsys.default") String host,
                                              OidcRestClient httpClient) {
        this.host = host;
        this.httpClient = httpClient;
    }

    public List<OrganisasjonsEnhet> hentEnheter(Saksbehandler saksbehandler) {
        if (Cluster.current().isProd()) return Collections.emptyList();
        var axsysRespons = httpClient.get(full_uri(saksbehandler), AxsysTilgangerResponse.class);
        return Optional.of(axsysRespons)
                .map(AxsysTilgangerResponse::getEnheter)
                .orElse(Collections.emptyList());

    }

    private URI full_uri(Saksbehandler saksbehandler) {
        return URI.create(host + PATH + saksbehandler.getSaksbehandlerIdent());
    }

    static class AxsysTilgangerResponse {
        private List<OrganisasjonsEnhet> enheter;

        public void setEnheter(List<OrganisasjonsEnhet> enheter) {
            this.enheter = enheter;
        }

        public List<OrganisasjonsEnhet> getEnheter() {
            return enheter;
        }
    }
}

