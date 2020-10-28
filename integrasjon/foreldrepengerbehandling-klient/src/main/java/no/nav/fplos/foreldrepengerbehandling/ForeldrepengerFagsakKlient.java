package no.nav.fplos.foreldrepengerbehandling;

import no.nav.fplos.foreldrepengerbehandling.dto.SokefeltDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import org.apache.http.client.utils.URIBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ForeldrepengerFagsakKlient {

    private static final String FAGSAK_SOK = "/fpsak/api/fagsak/sok";
    private OidcRestClient oidcRestClient;
    private String baseUrl;

    @Inject
    public ForeldrepengerFagsakKlient(OidcRestClient oidcRestClient,
                                      @KonfigVerdi(value = "fpsak.url", defaultVerdi = "http://fpsak") String baseUrl) {
        this.oidcRestClient = oidcRestClient;
        this.baseUrl = baseUrl;
    }

    public ForeldrepengerFagsakKlient() {
        // CDI
    }

    public List<FagsakDto> finnFagsaker(String søkestreng) {
        var uri = URI.create(baseUrl + FAGSAK_SOK);
        var sokefeltDto = new SokefeltDto(søkestreng);
        var fagsakDtoer = oidcRestClient.post(uri, sokefeltDto, FagsakDto[].class);
        return Arrays.asList(fagsakDtoer);
    }

    public <T> T get(URI href, Class<T> cls) {
        try {
            var uriBuilder = new URIBuilder(baseUrl);
            uriBuilder.setPath(href.getPath());
            uriBuilder.setCustomQuery(href.getQuery());
            URI uri = uriBuilder.build();
            return oidcRestClient.get(uri, cls);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Konstruksjon av uri til endepunkt som henter " + cls.getSimpleName() + " feiler", e);
        }
    }

}
