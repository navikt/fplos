package no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet;

import no.nav.foreldrepenger.loslager.aktør.OrganisasjonsEnhet;
import no.nav.foreldrepenger.loslager.organisasjon.Saksbehandler;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.util.env.Cluster;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SaksbehandlerEnhetstilgangTjeneste {

    private static final Logger log = LoggerFactory.getLogger(SaksbehandlerEnhetstilgangTjeneste.class);

    private static final String PATH = "/api/v1/tilgang/";
    private String host;
    private OidcRestClient httpClient;

    @Inject
    public SaksbehandlerEnhetstilgangTjeneste(@KonfigVerdi(value = "axsys.url", defaultVerdi = "http://axsys.default") String host,
                                              OidcRestClient httpClient) {
        this.host = host;
        this.httpClient = httpClient;
    }

    public SaksbehandlerEnhetstilgangTjeneste() {
        // CDI
    }

    public List<OrganisasjonsEnhet> hentEnheter(Saksbehandler saksbehandler) {
        var axsysRespons = httpClient.get(full_uri(saksbehandler), AxsysTilgangerResponse.class);
        return Optional.ofNullable(axsysRespons)
                .map(AxsysTilgangerResponse::getEnheter)
                .orElse(Collections.emptyList())
                .stream()
                .peek(this::loggfiltrerteEnheter)
                .filter(OrganisasjonsEnhet::kanBehandleForeldrepenger)
                .collect(Collectors.toList());
    }

    public List<OrganisasjonsEnhet> hentAktiveOgInaktiveEnheter(Saksbehandler saksbehandler) throws URISyntaxException {
        var uriString = host + PATH + saksbehandler.getSaksbehandlerIdent() + "/" + "?inkluderAlleEnheter=true";
        var uri = new URI(uriString);
        /*var uri = new URIBuilder()
                .setHost(host)
                .setPathSegments(PATH, saksbehandler.getSaksbehandlerIdent())
                .setParameter("inkluderAlleEnheter", "true")
                .build();*/
        var axsysMedInaktive = httpClient.get(uri, AxsysTilgangerResponse.class);
        return axsysMedInaktive.getEnheter();
    }

    private void loggfiltrerteEnheter(OrganisasjonsEnhet organisasjonsEnhet) {
        // greit å logge mens vi sammenlikner med norgtjenesten
        if (!organisasjonsEnhet.kanBehandleForeldrepenger()) {
            log.info("Fjerner enhet uten fagområde FOR: {}", organisasjonsEnhet.getEnhetId());
        }
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

